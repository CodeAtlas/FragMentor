package com.example.fragmentor.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fragmentor.app.controller.ArticleLoader;
import com.example.fragmentor.app.databinding.ArticleDetailFragmentBinding;
import com.example.fragmentor.app.model.Article;
import com.example.fragmentor.app.util.TrackingAnalyticsUtils;

/**
 * A fragment representing a single Article detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link ArticleDetailActivity}
 * on handsets.
 */
public class ArticleDetailFragment
        extends Fragment
        implements ArticleListFragment.Callbacks, LoaderManager.LoaderCallbacks<Article> {

    /* The fragment argument representing the item ID that this fragment represents. */
    public static final String ARG_ARTICLE_ID = "item_id";

    /* Costant key */
    private static final String KEY_DISPLAY_TIMING = "d_tm";

    /* ID of the article to be fetched from DB */
    private String articleId;

    /* ID for the article loader in the LoaderManager */
    private static final int LOADER_ARTICLE_ID = 42;

    /* User article display timing */
    private Long displayTiming;

    /* The article content will be displayed here */
    private ArticleDetailFragmentBinding binding;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            // previous state found, restore values
            if (savedInstanceState.containsKey(ARG_ARTICLE_ID)) {
                articleId = savedInstanceState.getString(ARG_ARTICLE_ID);
            }
            if (savedInstanceState.containsKey(KEY_DISPLAY_TIMING)) {
                displayTiming = savedInstanceState.getLong(KEY_DISPLAY_TIMING);
            }
        } else {
            // first run, check if who called also passed interesting arguments
            final Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_ARTICLE_ID)) {
                articleId = args.getString(ARG_ARTICLE_ID);
                displayTiming = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (articleId != null) {
            outState.putString(ARG_ARTICLE_ID, articleId);
        }
        if (displayTiming != null) {
            outState.putLong(KEY_DISPLAY_TIMING, displayTiming);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ArticleDetailFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (articleId != null) {
            // The call to forceLoad() is not necessary BUT it's useful to avoid a tricky bug of
            // AsyncTaskLoader (compat library) that doesn't call loadInBackgroud() when started
            getLoaderManager().initLoader(LOADER_ARTICLE_ID, null, this).forceLoad();
        }
    }

    @Override
    public void onArticleSelected(String id) {
        if (id != null && !id.equals(articleId)) {
            // A new article display request arrived!

            if (displayTiming != null) {
                // Tracking previous article display time
                TrackingAnalyticsUtils.sendTiming(getActivity(),
                        TrackingAnalyticsUtils.CAT_USER_TIMINGS,
                        System.currentTimeMillis() - displayTiming,
                        TrackingAnalyticsUtils.NAME_DISPLAY_TIMING,
                        null);
            }
            displayTiming = System.currentTimeMillis();
            articleId = id;

            // Tracking new article id
            TrackingAnalyticsUtils.sendEvent(getActivity(),
                    TrackingAnalyticsUtils.CAT_UI_ACTION,
                    TrackingAnalyticsUtils.ACT_ARTICLE_SELECTED,
                    articleId);

            // Create a new loader with new article parameter
            getLoaderManager().destroyLoader(LOADER_ARTICLE_ID);
            getLoaderManager().initLoader(LOADER_ARTICLE_ID, null, this).forceLoad();
        }
    }

    @Override
    public Loader<Article> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ARTICLE_ID:
                return new ArticleLoader(getActivity(), articleId);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Article> loader, Article data) {
        switch (loader.getId()) {
            case LOADER_ARTICLE_ID:
                binding.setArticle(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Article> loader) {
        switch (loader.getId()) {
            case LOADER_ARTICLE_ID:
                // Nothing to do.
                break;
        }
    }

}
