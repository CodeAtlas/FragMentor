package com.example.fragmentor.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.fragmentor.app.model.Article;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ArticlesDataSource {

    protected Context mContext;
    private SQLiteDatabase mDatabase;

    private static final String[] PROJECTION_ALL = {
            FragMentorSQLiteHelper.ARTICLES_COLUMN_CATEGORY,
            FragMentorSQLiteHelper.ARTICLES_COLUMN_ID,
            FragMentorSQLiteHelper.ARTICLES_COLUMN_TITLE,
            FragMentorSQLiteHelper.ARTICLES_COLUMN_CONTENT,
            FragMentorSQLiteHelper.ARTICLES_COLUMN_LINK,
            FragMentorSQLiteHelper.ARTICLES_COLUMN_DATE
    };

    public static final String DATA_CHANGED = ArticlesDataSource.class.getName() + ".DATA_CHANGED";

    public ArticlesDataSource(Context applicationContext) {
        mContext = applicationContext;
        FragMentorSQLiteHelper helper = new FragMentorSQLiteHelper(mContext);
        mDatabase = helper.getWritableDatabase();
    }

    protected void onDataChanged() {
        LocalBroadcastManager
                .getInstance(mContext)
                .sendBroadcast(new Intent(DATA_CHANGED));
    }

    public boolean insert(@NonNull Article entity) {

        long result;
        try {
            result = mDatabase.insertOrThrow(
                    FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                    null,
                    generateContentValuesFromObject(entity)
            );
        } catch (SQLException e) {
            result = -1;
        }

        if (result != -1) {
            // Se i dati sono cambiati notifico gli osservatori interessati
            onDataChanged();
        }

        return result != -1;
    }

    public boolean insert(@NonNull List<Article> entities) {

        long result;
        boolean inserted = false;

        mDatabase.beginTransaction();
        for(Article entity : entities) {
            try {
                result = mDatabase.insertOrThrow(
                        FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                        null,
                        generateContentValuesFromObject(entity)
                );
            } catch (SQLException e) {
                result = -1;
            }

            inserted |= (result != -1);
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();

        if (inserted) {
            // Se i dati sono cambiati notifico gli osservatori interessati
            onDataChanged();
        }

        return inserted;
    }

    public boolean delete(@NonNull Article entity) {

        int result = mDatabase.delete(
                FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                FragMentorSQLiteHelper.ARTICLES_COLUMN_ID + " = ?",
                new String[] {entity.id.get()}
        );

        if (result != 0) {
            // Se i dati sono cambiati notifico gli osservatori interessati
            onDataChanged();
        }

        return result != 0;
    }

    public boolean delete(@NonNull String selection, @NonNull String[] selectionArgs) {

        int result = mDatabase.delete(
                FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                selection,
                selectionArgs
        );

        if (result != 0) {
            // Se i dati sono cambiati notifico gli osservatori interessati
            onDataChanged();
        }

        return result != 0;
    }

    public boolean update(Article entity) {
        if (entity == null) {
            return false;
        }

        int result = mDatabase.update(
                FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                generateContentValuesFromObject(entity),
                FragMentorSQLiteHelper.ARTICLES_COLUMN_ID + " = ?",
                new String[] {entity.id.get()}
        );

        if (result != 0) {
            // Se i dati sono cambiati notifico gli osservatori interessati
            onDataChanged();
        }

        return result != 0;
    }

    public List<Article> read() {
        Cursor cursor = mDatabase.query(
                FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                PROJECTION_ALL,
                null,
                null,
                null,
                null,
                FragMentorSQLiteHelper.ARTICLES_COLUMN_DATE + " DESC"
        );

        List<Article> results = new ArrayList<Article>();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                results.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return results;
    }

    public List<Article> read(int category) {
        return read(
                FragMentorSQLiteHelper.ARTICLES_COLUMN_CATEGORY + " = ?",
                new String[]{String.valueOf(category)},
                null,
                null,
                FragMentorSQLiteHelper.ARTICLES_COLUMN_DATE + " DESC"
        );
    }

    public @Nullable Article read(String articleId) {
        final List<Article> dataList = read(
                FragMentorSQLiteHelper.ARTICLES_COLUMN_ID + " = ?",
                new String[]{articleId},
                null,
                null,
                null
        );

        if (dataList.size() > 0) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    public List<Article> read(String selection, String[] selectionArgs,
                              String groupBy, String having, String orderBy) {

        Cursor cursor = mDatabase.query(
                FragMentorSQLiteHelper.ARTICLES_TABLE_NAME,
                PROJECTION_ALL,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy
        );

        List<Article> results = new ArrayList<Article>();
        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                results.add(generateObjectFromCursor(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return results;
    }

    public @Nullable Article generateObjectFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        return new Article(
                cursor.getInt(cursor.getColumnIndex(FragMentorSQLiteHelper.ARTICLES_COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndex(FragMentorSQLiteHelper.ARTICLES_COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(FragMentorSQLiteHelper.ARTICLES_COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(FragMentorSQLiteHelper.ARTICLES_COLUMN_CONTENT)),
                cursor.getString(cursor.getColumnIndex(FragMentorSQLiteHelper.ARTICLES_COLUMN_LINK)),
                new Date(cursor.getLong(cursor.getColumnIndex(FragMentorSQLiteHelper.ARTICLES_COLUMN_DATE)))
        );
    }

    public ContentValues generateContentValuesFromObject(@NonNull Article entity) {
        ContentValues values = new ContentValues();
        values.put(FragMentorSQLiteHelper.ARTICLES_COLUMN_CATEGORY, entity.category.get());
        values.put(FragMentorSQLiteHelper.ARTICLES_COLUMN_ID, entity.id.get());
        values.put(FragMentorSQLiteHelper.ARTICLES_COLUMN_TITLE, entity.title.get());
        values.put(FragMentorSQLiteHelper.ARTICLES_COLUMN_CONTENT, entity.content.get());
        values.put(FragMentorSQLiteHelper.ARTICLES_COLUMN_LINK, entity.link.get());
        values.put(FragMentorSQLiteHelper.ARTICLES_COLUMN_DATE, entity.date.get().getTime());
        return values;
    }
}
