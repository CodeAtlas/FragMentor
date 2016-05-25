package com.example.fragmentor.app.databinding;


import android.databinding.BindingAdapter;
import android.webkit.WebView;

public class Bindings {

    @BindingAdapter("bind:htmlContent")
    public static void setHtmlContent(WebView webView, String html){
        webView.loadData(html, "text/html", "UTF-8");
    }

}
