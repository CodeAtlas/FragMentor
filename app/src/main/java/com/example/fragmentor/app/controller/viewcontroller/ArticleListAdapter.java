package com.example.fragmentor.app.controller.viewcontroller;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.fragmentor.app.R;
import com.example.fragmentor.app.model.Article;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;


public class ArticleListAdapter implements ListAdapter {

    LayoutInflater inflater;
    ArrayList<Article> articles = new ArrayList<Article>();
    ArrayList<DataSetObserver> observers = new ArrayList<DataSetObserver>();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.ITALY);

    public ArticleListAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public ArticleListAdapter(@NonNull Context context, @NonNull Collection<Article> data) {
        this.inflater = LayoutInflater.from(context);
        this.articles.addAll(data);
    }

    private class ViewHolder {
        public TextView text1;
        public TextView text2;

        private ViewHolder(@NonNull View rootView) {
            text1 = (TextView) rootView.findViewById(android.R.id.text1);
            text2 = (TextView) rootView.findViewById(android.R.id.text2);
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
        return 0l;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem_article, parent, false);
            if (convertView == null) {
                return null;
            }
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text1.setText(articles.get(position).title);
        holder.text2.setText(dateFormatter.format(articles.get(position).date));

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
