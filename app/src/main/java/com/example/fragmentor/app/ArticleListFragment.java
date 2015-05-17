package com.example.fragmentor.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.example.fragmentor.app.controller.ArticleListLoader;
import com.example.fragmentor.app.controller.viewcontroller.ArticleListAdapter;
import com.example.fragmentor.app.model.Article;
import com.example.fragmentor.app.service.RssDownloadService;

import java.util.List;

/**
 * A list fragment representing a list of Feed. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ArticleDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ArticleListFragment
        extends ListFragment
        implements LoaderManager.LoaderCallbacks<List<Article>>, ArticleCategoriesFragment.Callbacks {

    private ArticleListAdapter articleAdapter;
    private int currentCategory;
    private Integer currentSelection;

    /*  Id Loader */
    private static final int LOADER_ARTICLES_ID = 58845;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private static final String STATE_CATEGORY = "category";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        void onArticleSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onArticleSelected(String id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Avviso che contribuisco a riempire le Action della ActionBar
        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            currentCategory = 0;
        } else {
            currentCategory = savedInstanceState.getInt(STATE_CATEGORY);
        }

        articleAdapter = new ArticleListAdapter(getActivity());
        setListAdapter(articleAdapter);

        // The call to forceLoad() is not necessary BUT it's useful to avoid a tricky bug of
        // AsyncTaskLoader (compat library) that doesn't call loadInBackgroud() when started
        getLoaderManager().initLoader(LOADER_ARTICLES_ID, null, this).forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.refresh, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Intent i = new Intent(getActivity(), RssDownloadService.class);
                getActivity().startService(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            currentSelection = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onArticleSelected(((Article) articleAdapter.getItem(position)).id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
            outState.putInt(STATE_CATEGORY, currentCategory);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    @Override
    public void onCategorySelected(int categoryIndex) {
        getLoaderManager().destroyLoader(LOADER_ARTICLES_ID);
        currentCategory = categoryIndex;
        getLoaderManager().initLoader(LOADER_ARTICLES_ID, null, this).forceLoad();
    }

    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ARTICLES_ID:
                return new ArticleListLoader(getActivity(), currentCategory);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        switch (loader.getId()) {
            case LOADER_ARTICLES_ID:
                articleAdapter.clear();
                articleAdapter.addAll(data);

                if (currentSelection != null) {
                    setActivatedPosition(currentSelection);
                    currentSelection = null;
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        switch (loader.getId()) {
            case LOADER_ARTICLES_ID:
                articleAdapter.clear();
                break;
        }
    }

}
