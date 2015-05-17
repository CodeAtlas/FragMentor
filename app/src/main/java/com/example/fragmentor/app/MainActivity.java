package com.example.fragmentor.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


/**
 * An activity representing a list of Feed. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ArticleDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ArticleListFragment} and the item details
 * (if present) is a {@link ArticleDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ArticleListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class MainActivity
        extends AppCompatActivity
        implements ArticleListFragment.Callbacks, ArticleCategoriesFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.article_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (mTwoPane) {
            if (savedInstanceState == null) {
                // At first run, add the detail fragment. When there's a saved state
                // previous Fragments are added back automatically.
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.article_detail_container, new ArticleDetailFragment())
                        .commit();
            }

            // In two-pane mode, list items should be given the 'activated' state when touched.
            ((ArticleListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.article_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onCategorySelected(int categoryIndex) {
        // Categories fragment tells me the user has made a choice.
        // Notify List fragment about it!
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.article_list);
        if (f != null && f instanceof ArticleCategoriesFragment.Callbacks) {
            ((ArticleCategoriesFragment.Callbacks) f).onCategorySelected(categoryIndex);
        }
    }

    /**
     * Callback method from {@link ArticleListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onArticleSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.article_detail_container);

            if (f instanceof ArticleDetailFragment) {
                ((ArticleDetailFragment) f).onArticleSelected(id);
            }

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ArticleDetailActivity.class);
            detailIntent.putExtra(ArticleDetailFragment.ARG_ARTICLE_ID, id);
            startActivity(detailIntent);
        }
    }

}
