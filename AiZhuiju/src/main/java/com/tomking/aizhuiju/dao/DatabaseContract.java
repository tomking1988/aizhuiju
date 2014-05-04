package com.tomking.aizhuiju.dao;

import android.provider.BaseColumns;

/**
 * Created by xtang on 14-4-7.
 */
public final class DatabaseContract {

    public static abstract class CurrentShow implements BaseColumns {

        public static final String TABLE_NAME = "current_show";
        public static final String COLUMN_NAME_SHOW_ID = "show_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_ALT_TITLE = "alt_title";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_SUMMARY = "summary";
        public static final String COLUMN_NAME_PUBDATE = "pubdate";
        public static final String COLUMN_NAME_RATING = "rating";

    }

    public static abstract class Episodes implements BaseColumns {

        public static final String TABLE_NAME = "episodes";
        public static final String COLUMN_NAME_SHOW_ID = "show_id";
        public static final String COLUMN_NAME_SHOW_NAME = "show_name";
        public static final String COLUMN_NAME_SEASON = "season";
        public static final String COLUMN_NAME_EPISODE_NUMBER = "episode_num";
        public static final String COLUMN_NAME_PUBDATE = "pubdate";
        public static final String COLUMN_NAME_EPISODE_NAME = "episode_name";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";
        public static final String COLUMN_NAME_EPISODE_TYPE = "type";
    }

    public static abstract class SQLiteCommon {
        public static final String TEXT_TYPE = " TEXT ";
        public static final String REAL_TYPE = " REAL ";
        public static final String INTEGER_TYPE = " INTEGER ";

        public static final String COMMA_SEP = ",";
    }
}
