package com.example.fragmentor.app.model;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;

import java.util.Date;

public class Article {
    public final ObservableInt category = new ObservableInt();
    public final ObservableField<String> id = new ObservableField<>();
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> content = new ObservableField<>();
    public final ObservableField<String> link = new ObservableField<>();
    public final ObservableField<Date> date = new ObservableField<>();

    public Article(
            int category,
            @NonNull String id,
            String title,
            String content,
            String link,
            Date date
    ) {
        this.category.set(category);
        this.id.set(id);
        this.title.set(title);
        this.content.set(content);
        this.link.set(link);
        this.date.set(date);
    }

    @Override
    public String toString() {
        return "Article{" +
                "category=" + category +
                ", id=" + id +
                ", title=" + title +
                ", content=" + content +
                ", link=" + link +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Article article = (Article) o;

        if (!category.equals(article.category)) return false;
        return id.equals(article.id);

    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
