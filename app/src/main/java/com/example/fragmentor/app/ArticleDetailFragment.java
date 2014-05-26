package com.example.fragmentor.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.fragmentor.app.controller.ArticleLoader;
import com.example.fragmentor.app.model.Article;

/**
 * A fragment representing a single Article detail screen.
 * This fragment is either contained in a {@link ArticleMainActivity}
 * in two-pane mode (on tablets) or a {@link ArticleDetailActivity}
 * on handsets.
 */
public class ArticleDetailFragment
        extends Fragment
        implements LoaderManager.LoaderCallbacks<Article>
{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /* Id dell'articolo da caricare dal DB */
    private String articleId;

    /* ID del fragment sul loader manager */
    private static final int LOADER_ARTICLE_ID = 42;

    /* Parti della view */
    private WebView detailWebView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            articleId = getArguments().getString(ARG_ITEM_ID);
            // The call to forceLoad() is not necessary BUT it's useful to avoid a tricky bug of
            // AsyncTaskLoader (compat library) that doesn't call loadInBackgroud() when started
            getLoaderManager().initLoader(LOADER_ARTICLE_ID, null, this).forceLoad();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        if (rootView == null) {
            return null;
        }

        // Show the dummy content as text in a TextView.
        detailWebView = ((WebView) rootView.findViewById(R.id.article_detail));

        return rootView;
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
                detailWebView.loadData(data.description, "text/html", "UTF-8");
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
