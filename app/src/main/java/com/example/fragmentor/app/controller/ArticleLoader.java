package com.example.fragmentor.app.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import com.example.fragmentor.app.db.ArticlesDataSource;
import com.example.fragmentor.app.model.Article;

public class ArticleLoader extends AsyncTaskLoader<Article> {

    private ArticlesDataSource dataSource;
    private DataChangeObserver changeObserver;
    private Article mLastData;
    private String articleId;

    public ArticleLoader(Context context, String articleId) {
        super(context);
        changeObserver = new DataChangeObserver();
        changeObserver.registerReceiver(getContext());
        this.articleId = articleId;
        dataSource = new ArticlesDataSource(context);
    }

    @Override
    protected void finalize() throws Throwable {
        changeObserver.unregisterReceiver(getContext());
        super.finalize();
    }

    @Override
    public Article loadInBackground() {
        mLastData = dataSource.read(articleId);
        return mLastData;
    }

    @Override
    public void deliverResult(Article data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        Article oldData = mLastData;
        mLastData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Now we can let oldData to be garbage collected
        oldData = null;
    }


    private class DataChangeObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // When a DATA_CHANGED intent is received, notify the Loader to load the new data!
            onContentChanged();
        }

        public void registerReceiver(Context applicationContext) {
            LocalBroadcastManager
                    .getInstance(applicationContext)
                    .registerReceiver(this, new IntentFilter(ArticlesDataSource.DATA_CHANGED));
        }

        public void unregisterReceiver(Context applicationContext) {
            LocalBroadcastManager
                    .getInstance(applicationContext)
                    .unregisterReceiver(this);
        }
    }

}
