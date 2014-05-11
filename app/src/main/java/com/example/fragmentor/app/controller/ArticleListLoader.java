package com.example.fragmentor.app.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import com.example.fragmentor.app.db.ArticlesDataSource;
import com.example.fragmentor.app.model.Article;

import java.util.List;

public class ArticleListLoader extends AsyncTaskLoader<List<Article>> {

    private ArticlesDataSource dataSource;
    private DataChangeObserver changeObserver;
    private List<Article> mLastDataList;
    private int category;

    public ArticleListLoader(Context context, int category) {
        super(context);
        changeObserver = new DataChangeObserver();
        changeObserver.registerReceiver(getContext());
        this.category = category;
        dataSource = new ArticlesDataSource(context);
    }

    @Override
    protected void finalize() throws Throwable {
        changeObserver.unregisterReceiver(getContext());
        super.finalize();
    }

    @Override
    public List<Article> loadInBackground() {
        mLastDataList = dataSource.read(category);
        return mLastDataList;
    }

    @Override
    public void deliverResult(List<Article> data) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        List<Article> oldDataList = mLastDataList;
        mLastDataList = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Now we can let oldDataList and its content to be garbage collected
        oldDataList.clear();
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
