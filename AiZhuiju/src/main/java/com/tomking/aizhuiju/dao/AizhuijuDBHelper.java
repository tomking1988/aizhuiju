package com.tomking.aizhuiju.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.douban.models.Movie;
import com.tomking.aizhuiju.dao.DatabaseContract.*;

/**
 * Created by xtang on 14-4-7.
 */
public class AizhuijuDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Aizhuiju.db";


    public AizhuijuDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_CURRENT_SHOW =
            "CREATE TABLE " + CurrentShow.TABLE_NAME + " (" +
                    CurrentShow._ID + " INTEGER PRIMARY KEY," +
                    CurrentShow.COLUMN_NAME_SHOW_ID + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    CurrentShow.COLUMN_NAME_TITLE + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    CurrentShow.COLUMN_NAME_ALT_TITLE + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    CurrentShow.COLUMN_NAME_IMAGE + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    CurrentShow.COLUMN_NAME_PUBDATE + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    CurrentShow.COLUMN_NAME_SUMMARY + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    CurrentShow.COLUMN_NAME_RATING + SQLiteCommon.REAL_TYPE +
            " )";

    public static final String SQL_CREATE_EPISODES =
            "CREATE TABLE " + Episodes.TABLE_NAME + " (" +
                    Episodes._ID + " INTEGER PRIMARY KEY," +
                    Episodes.COLUMN_NAME_SHOW_ID + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_EPISODE_NAME + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_SEASON + SQLiteCommon.INTEGER_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_EPISODE_NUMBER + SQLiteCommon.INTEGER_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_PUBDATE + SQLiteCommon.TEXT_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_UPDATED_AT + SQLiteCommon.INTEGER_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_EPISODE_TYPE + SQLiteCommon.INTEGER_TYPE + SQLiteCommon.COMMA_SEP +
                    Episodes.COLUMN_NAME_SHOW_NAME + SQLiteCommon.TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_CURRENT_SHOW =
            "DROP TABLE IF EXISTS " + CurrentShow.TABLE_NAME;

    public static final String SQL_DELETE_EPISODES =
            "DROP TABLE IF EXISTS " + Episodes.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CURRENT_SHOW);
        db.execSQL(SQL_CREATE_EPISODES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CURRENT_SHOW);
        db.execSQL(SQL_DELETE_EPISODES);
        onCreate(db);
    }


}
