package com.example.fragmentor.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FragMentorSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fragmentor.db";
    private static final int DATABASE_VERSION = 2;

    public static final String ARTICLES_TABLE_NAME = "articles";
    public static final String ARTICLES_COLUMN_CATEGORY = "category";
    public static final String ARTICLES_COLUMN_ID = "id";
    public static final String ARTICLES_COLUMN_TITLE = "title";
    public static final String ARTICLES_COLUMN_CONTENT = "content";
    public static final String ARTICLES_COLUMN_LINK = "link";
    public static final String ARTICLES_COLUMN_DATE = "date";
    private static final String ARTICLES_TABLE_CREATE = "CREATE TABLE "
            + ARTICLES_TABLE_NAME + "( "
            + ARTICLES_COLUMN_CATEGORY + " INT NOT NULL, "
            + ARTICLES_COLUMN_ID + " TEXT PRIMARY KEY, "
            + ARTICLES_COLUMN_TITLE + " TEXT, "
            + ARTICLES_COLUMN_CONTENT + " TEXT, "
            + ARTICLES_COLUMN_LINK + " TEXT, "
            + ARTICLES_COLUMN_DATE + " INT );";
    private static final String ARTICLES_TABLE_DROP = "DROP TABLE IF EXISTS " + ARTICLES_TABLE_NAME;


    public FragMentorSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(ARTICLES_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(FragMentorSQLiteHelper.class.getSimpleName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");

        // TODO definire un vero metodo di upgrade, se necessario!

        db.execSQL(ARTICLES_TABLE_DROP);
        onCreate(db);
    }

}