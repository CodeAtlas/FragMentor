package com.example.fragmentor.app.model;

import android.support.annotation.NonNull;

import java.util.Date;

public class Article {
    public final int category;
    public final String id;
    public final String title;
    public final String content;
    public final String link;
    public final Date date;

    public Article(
            int category,
            @NonNull String id,
            String title,
            String content,
            String link,
            Date date
    ) {
        this.category = category;
        this.id = id;
        this.title = title;
        this.content = content;
        this.link = link;
        this.date = date;
    }

    @Override
    public String toString() {
        return title;
    }
}
