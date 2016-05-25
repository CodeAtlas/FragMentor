package com.example.fragmentor.app.controller.viewcontroller;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.example.fragmentor.app.databinding.ArticleListItemBinding;
import com.example.fragmentor.app.model.Article;

import java.util.ArrayList;
import java.util.Collection;


public class ArticleListAdapter implements ListAdapter {

    LayoutInflater inflater;
    ArrayList<Article> articles = new ArrayList<>();
    ArrayList<DataSetObserver> observers = new ArrayList<>();

    public ArticleListAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public ArticleListAdapter(@NonNull Context context, @NonNull Collection<Article> data) {
        this.inflater = LayoutInflater.from(context);
        this.articles.addAll(data);
    }

    private static class ArticleViewHolder {
        public ArticleListItemBinding binding;

        private ArticleViewHolder(LayoutInflater inflater, ViewGroup viewGroup) {
            binding = ArticleListItemBinding.inflate(inflater, viewGroup, false);
        }
    }

    public void clear() {
        articles.clear();
        for (DataSetObserver o : observers) {
            o.onChanged();
        }
    }

    public void addAll(Collection<Article> articles) {
        this.articles.addAll(articles);
        for (DataSetObserver o : observers) {
            o.onChanged();
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position >= articles.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observers.remove(observer);
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0 && position < articles.size()) {
            return articles.get(position).hashCode();
        }
        return 0L;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ArticleViewHolder holder;

        if (convertView == null) {
            holder = new ArticleViewHolder(inflater, parent);
            convertView = holder.binding.getRoot();
            convertView.setTag(holder);
        } else {
            holder = (ArticleViewHolder) convertView.getTag();
        }

        holder.binding.setArticle(articles.get(position));

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return articles.isEmpty();
    }
}
