package com.example.fragmentor.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.fragmentor.app.R;
import com.example.fragmentor.app.controller.AgiRssParser;
import com.example.fragmentor.app.db.ArticlesDataSource;
import com.example.fragmentor.app.db.FragMentorSQLiteHelper;
import com.example.fragmentor.app.model.Article;
import com.example.fragmentor.app.util.TrackingAnalyticsUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RssDownloadService extends IntentService {

    private static final String TAG = RssDownloadService.class.getSimpleName();
    private static final int EXPIRATION = 3 * 24 * 60 * 60 * 1000;

    public RssDownloadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String[] agiNewsFeeds = getResources().getStringArray(R.array.agiNewsFeedsUrls);

        HttpClient httpclient = new DefaultHttpClient();
        AgiRssParser parser = new AgiRssParser();
        ArticlesDataSource dataSource = new ArticlesDataSource(getApplicationContext());

        // Rimuovo gli articoli vecchi
        dataSource.delete(
                FragMentorSQLiteHelper.ARTICLES_COLUMN_DATE + " < ?",
                new String[] {String.valueOf(System.currentTimeMillis() - EXPIRATION)}
        );

        // Cerco gli articoli nuovi
        for (int category = 0; category < agiNewsFeeds.length; category++) {

            List<Article> results = null;
            HttpGet httpGet = new HttpGet(agiNewsFeeds[category]);
            HttpResponse response;
            long downloadTiming = System.currentTimeMillis();

            try {
                response = httpclient.execute(httpGet);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream is = entity.getContent();
                    results = parser.parse(category, is);
                }
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }  catch (XmlPullParserException e) {
                Log.e(TAG, e.getMessage());
            }

            // Tracking RSS download time
            TrackingAnalyticsUtils.sendTiming(getApplicationContext(),
                    TrackingAnalyticsUtils.CAT_SYSTEM_TIMINGS,
                    System.currentTimeMillis() - downloadTiming,
                    TrackingAnalyticsUtils.NAME_DOWNLOAD_TIMING,
                    null);

            if (results != null) {
                // Inserisco tutto!
                dataSource.insert(results);
            }
        }
    }

}
