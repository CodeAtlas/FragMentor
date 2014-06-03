package com.example.fragmentor.app.util;

import android.content.Context;
import android.support.annotation.Nullable;

import com.example.fragmentor.app.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class TrackingAnalyticsUtils {

    // Analytics Categories
    public static final String CAT_UI_ACTION = "ui_action";
    public static final String CAT_SYSTEM_TIMINGS = "system_timings";
    public static final String CAT_USER_TIMINGS = "user_timings";

    // Event actions
    public static final String ACT_CATEGORY_SELECTED = "category_selected";
    public static final String ACT_ARTICLE_SELECTED = "article_selected";

    public static final String NAME_DOWNLOAD_TIMING = "download_timing";
    public static final String NAME_DISPLAY_TIMING = "display_timing";

    private static Tracker appTracker = null;

    public static synchronized Tracker getTracker(Context context) {
        if (appTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            appTracker = analytics.newTracker(R.xml.analytics_tracker);
        }
        return appTracker;
    }

    /**
     * Make and send an Analytics Event
     * @param context Context
     * @param category Event category
     * @param action Event action
     * @param label Event name (can be null)
     */
    public static void sendEvent(Context context, String category, String action, @Nullable String label) {

        Tracker tracker = getTracker(context);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    /**
     * Create and send an Analytics Timing
     * @param context Context
     * @param category Timing category
     * @param timing Time in millisecond
     * @param name Timing event (can be null)
     * @param label Timing name (can be null)
     */
    public static void sendTiming(Context context, String category, long timing, @Nullable String name, @Nullable String label) {

        Tracker tracker = getTracker(context);
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory(category)
                .setValue(timing)
                .setVariable(name)
                .setLabel(label)
                .build());
    }
}
