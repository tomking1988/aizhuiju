package com.tomking.aizhuiju.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.douban.models.Movie;
import com.tomking.aizhuiju.common.DateHelper;
import com.tomking.aizhuiju.dao.DatabaseContract.*;
import com.tomking.aizhuiju.test.MyLog;
import com.tvrage.models.EpisodeInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by xtang on 14-4-7.
 */
public class AizhuijuDBManager {
    private static AizhuijuDBHelper dbHelper;
    private static SQLiteDatabase db;
    private static AizhuijuDBManager instance;

    private AizhuijuDBManager() {}

    public static AizhuijuDBManager getInstance(Context context) {
        if(instance == null) {
            synchronized (AizhuijuDBManager.class) {
                instance = new AizhuijuDBManager();
                dbHelper = new AizhuijuDBHelper(context);
                db = dbHelper.getWritableDatabase();
            }
        }
        return instance;
    }

    public void close() {
        try{
            db.close();
            db = null;
        } catch (Exception e) {
            MyLog.d("database manager close exception "+e.toString());
        }

    }

    public void connect() {
        if(db == null) {
            db = dbHelper.getWritableDatabase();
        } else {
            if(db.isOpen())
                return;
            else {
                db = dbHelper.getWritableDatabase();
            }
        }
    }

    public long addCurrentShow(Movie show) {

        if(existInCurrentShow(show))
            return -1;

        ContentValues values = new ContentValues();
        values.put(CurrentShow.COLUMN_NAME_SHOW_ID, show.getIdLong());
        values.put(CurrentShow.COLUMN_NAME_TITLE, show.getTitle());
        values.put(CurrentShow.COLUMN_NAME_ALT_TITLE, show.getAltTitle());
        values.put(CurrentShow.COLUMN_NAME_IMAGE, show.getImage());
        values.put(CurrentShow.COLUMN_NAME_SUMMARY, show.getSummary());
        values.put(CurrentShow.COLUMN_NAME_RATING, Double.parseDouble(show.getAverageRating()));
        values.put(CurrentShow.COLUMN_NAME_PUBDATE, show.getPubdate());


        return db.insert(
                CurrentShow.TABLE_NAME,
                null,
                values);
    }

    public Movie getCurrentShowByID(String id) {

        String[] projection = {
                CurrentShow._ID,
                CurrentShow.COLUMN_NAME_TITLE,
                CurrentShow.COLUMN_NAME_ALT_TITLE,
                CurrentShow.COLUMN_NAME_IMAGE,
                CurrentShow.COLUMN_NAME_SHOW_ID,
                CurrentShow.COLUMN_NAME_SUMMARY,
                CurrentShow.COLUMN_NAME_PUBDATE,
                CurrentShow.COLUMN_NAME_RATING

        };

        Cursor c = db.query(
                CurrentShow.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                CurrentShow.COLUMN_NAME_SHOW_ID + " = " + id ,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        c.moveToNext();
        if(c.isAfterLast())
            return null;

        Movie show = new Movie(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_SHOW_ID)));
        show.setTitle(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_TITLE)));
        show.setAlt_title(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_ALT_TITLE)));
        show.setImage(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_IMAGE)));
        show.setSummary(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_SUMMARY)));
        show.setAverageRating(c.getDouble(c.getColumnIndex(CurrentShow.COLUMN_NAME_RATING)));
        show.setPubdate(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_PUBDATE)));



        c.close();
        return show;
    }

    public boolean existInCurrentShow(Movie show) {
         String query = "SELECT COUNT(*) AS NUM FROM " + CurrentShow.TABLE_NAME + " WHERE " +
                 CurrentShow.COLUMN_NAME_SHOW_ID + " = " + show.getIdLong() + " OR " +
                 CurrentShow.COLUMN_NAME_TITLE + " = '" + show.getTitle() + "'";
         Cursor c = db.rawQuery(query, null);
         c.moveToNext();
        boolean result = c.getInt(c.getColumnIndex("NUM")) > 0;
        c.close();
         return result;
    }

    public int deleteCurrentShow(String showID) {
        return db.delete(CurrentShow.TABLE_NAME, CurrentShow.COLUMN_NAME_SHOW_ID + "=" + showID, null);
    }

    public ArrayList<Movie> getCurrentShows() {
        ArrayList<Movie> currentShows = new ArrayList<Movie>();
        String[] projection = {
                CurrentShow._ID,
                CurrentShow.COLUMN_NAME_TITLE,
                CurrentShow.COLUMN_NAME_ALT_TITLE,
                CurrentShow.COLUMN_NAME_IMAGE,
                CurrentShow.COLUMN_NAME_SHOW_ID,
                CurrentShow.COLUMN_NAME_SUMMARY,
                CurrentShow.COLUMN_NAME_PUBDATE,
                CurrentShow.COLUMN_NAME_RATING

        };

        Cursor c = db.query(
                CurrentShow.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while(c.moveToNext()) {
            Movie show = new Movie(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_SHOW_ID)));
            show.setTitle(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_TITLE)));
            show.setAlt_title(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_ALT_TITLE)));
            show.setImage(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_IMAGE)));
            show.setSummary(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_SUMMARY)));
            show.setAverageRating(c.getDouble(c.getColumnIndex(CurrentShow.COLUMN_NAME_RATING)));
            show.setPubdate(c.getString(c.getColumnIndex(CurrentShow.COLUMN_NAME_PUBDATE)));
            currentShows.add(show);
        }

        c.close();
        return currentShows;
    }

    public long addEpisodeInfo(EpisodeInfo episodeInfo) {
        if(hasEpisodeInfo(episodeInfo))
            return -1;

        ContentValues values = new ContentValues();
        values.put(Episodes.COLUMN_NAME_SHOW_NAME, episodeInfo.getShowName());
        values.put(Episodes.COLUMN_NAME_SHOW_ID, episodeInfo.getShowID());
        values.put(Episodes.COLUMN_NAME_EPISODE_NAME, episodeInfo.getEpisodeName());
        values.put(Episodes.COLUMN_NAME_EPISODE_TYPE, episodeInfo.getEpisodeType());
        values.put(Episodes.COLUMN_NAME_UPDATED_AT, DateHelper.getCurrentMilliseconds());
        values.put(Episodes.COLUMN_NAME_SEASON, episodeInfo.getSeason());
        values.put(Episodes.COLUMN_NAME_EPISODE_NUMBER, episodeInfo.getEpisodeNum());
        values.put(Episodes.COLUMN_NAME_PUBDATE, episodeInfo.getPubdate());

        //MyLog.d("add episode info into database " +episodeInfo.getShowName() + " " + episodeInfo.getPubdate());
        return db.insert(
                Episodes.TABLE_NAME,
                null,
                values);
    }

    public boolean hasEpisodeInfo(EpisodeInfo episodeInfo) {
        Cursor c = db.query(
                Episodes.TABLE_NAME,  // The table to query
                new String[] {Episodes._ID},                               // The columns to return
                Episodes.COLUMN_NAME_EPISODE_TYPE + " = " + episodeInfo.getEpisodeType() + " AND " +
                Episodes.COLUMN_NAME_SHOW_NAME + " = '" + episodeInfo.getShowName() + "'",                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        boolean result = c.moveToNext();
        c.close();
        return result;
    }

    public boolean isUpdatedThisWeekEpisodes() {
        Cursor c = db.query(
                Episodes.TABLE_NAME,
                new String[]{Episodes.COLUMN_NAME_UPDATED_AT},
                null,
                null,
                null,
                null,
                null);
        if(!c.moveToNext()) {
            return false;
        }
        long milliseconds = c.getLong(c.getColumnIndex(Episodes.COLUMN_NAME_UPDATED_AT));
        c.close();
        Date updatedDate = new Date(milliseconds);
        return DateHelper.isWithinThisWeek(updatedDate);
    }

    public ArrayList<EpisodeInfo> getLatestEpisodes() {
        ArrayList<EpisodeInfo> latestEpisodes = new ArrayList<EpisodeInfo>();
        String[] projection = {
                Episodes.COLUMN_NAME_SHOW_ID,
                Episodes.COLUMN_NAME_UPDATED_AT,
                Episodes.COLUMN_NAME_SHOW_NAME,
                Episodes.COLUMN_NAME_SEASON,
                Episodes.COLUMN_NAME_PUBDATE,
                Episodes.COLUMN_NAME_EPISODE_NUMBER,
                Episodes.COLUMN_NAME_EPISODE_NAME,
                Episodes.COLUMN_NAME_EPISODE_TYPE

        };

        Cursor c = db.query(
                Episodes.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                Episodes.COLUMN_NAME_EPISODE_TYPE + " = " + EpisodeInfo.LATEST_EPISODE_TYPE,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while(c.moveToNext()) {
            EpisodeInfo episodeInfo = new EpisodeInfo();
            episodeInfo.setEpisodeType(c.getInt(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_TYPE)));
            episodeInfo.setShowName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_NAME)));
            episodeInfo.setShowID(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_ID)));
            episodeInfo.setSeason(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SEASON)));
            episodeInfo.setEpisodeNum(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NUMBER)));
            episodeInfo.setEpisodeName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NAME)));
            episodeInfo.setPubdate(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_PUBDATE)));
            latestEpisodes.add(episodeInfo);
        }

        c.close();
        return latestEpisodes;
    }

    public ArrayList<EpisodeInfo> getAllEpisodes() {
        ArrayList<EpisodeInfo> allEpisodes = new ArrayList<EpisodeInfo>();
        String[] projection = {
                Episodes.COLUMN_NAME_SHOW_ID,
                Episodes.COLUMN_NAME_UPDATED_AT,
                Episodes.COLUMN_NAME_SHOW_NAME,
                Episodes.COLUMN_NAME_SEASON,
                Episodes.COLUMN_NAME_PUBDATE,
                Episodes.COLUMN_NAME_EPISODE_NUMBER,
                Episodes.COLUMN_NAME_EPISODE_NAME,
                Episodes.COLUMN_NAME_EPISODE_TYPE

        };

        Cursor c = db.query(
                Episodes.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while(c.moveToNext()) {
            EpisodeInfo episodeInfo = new EpisodeInfo();
            episodeInfo.setEpisodeType(c.getInt(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_TYPE)));
            episodeInfo.setShowName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_NAME)));
            episodeInfo.setShowID(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_ID)));
            episodeInfo.setSeason(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SEASON)));
            episodeInfo.setEpisodeNum(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NUMBER)));
            episodeInfo.setEpisodeName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NAME)));
            episodeInfo.setPubdate(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_PUBDATE)));
            allEpisodes.add(episodeInfo);
        }

        c.close();
        return allEpisodes;
    }

    public ArrayList<EpisodeInfo> getNextEpisodes() {
        ArrayList<EpisodeInfo> thisWeekEpisodes = new ArrayList<EpisodeInfo>();
        String[] projection = {
                Episodes.COLUMN_NAME_SHOW_ID,
                Episodes.COLUMN_NAME_UPDATED_AT,
                Episodes.COLUMN_NAME_SHOW_NAME,
                Episodes.COLUMN_NAME_SEASON,
                Episodes.COLUMN_NAME_PUBDATE,
                Episodes.COLUMN_NAME_EPISODE_NUMBER,
                Episodes.COLUMN_NAME_EPISODE_NAME,
                Episodes.COLUMN_NAME_EPISODE_TYPE

        };

        Cursor c = db.query(
                CurrentShow.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                Episodes.COLUMN_NAME_EPISODE_TYPE + " = " + EpisodeInfo.NEXT_EPISODE_TYPE,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while(c.moveToNext()) {
            EpisodeInfo episodeInfo = new EpisodeInfo();
            episodeInfo.setEpisodeType(c.getInt(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_TYPE)));
            episodeInfo.setShowName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_NAME)));
            episodeInfo.setShowID(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_ID)));
            episodeInfo.setSeason(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SEASON)));
            episodeInfo.setEpisodeNum(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NUMBER)));
            episodeInfo.setEpisodeName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NAME)));
            episodeInfo.setPubdate(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_PUBDATE)));
            thisWeekEpisodes.add(episodeInfo);
        }

        c.close();
        return thisWeekEpisodes;
    }

    public ArrayList<EpisodeInfo> getThisWeekEpisodes() {
        ArrayList<EpisodeInfo> thisWeekEpisodes = new ArrayList<EpisodeInfo>();
        String[] projection = {
                Episodes.COLUMN_NAME_SHOW_ID,
                Episodes.COLUMN_NAME_UPDATED_AT,
                Episodes.COLUMN_NAME_SHOW_NAME,
                Episodes.COLUMN_NAME_SEASON,
                Episodes.COLUMN_NAME_PUBDATE,
                Episodes.COLUMN_NAME_EPISODE_NUMBER,
                Episodes.COLUMN_NAME_EPISODE_NAME,
                Episodes.COLUMN_NAME_EPISODE_TYPE

        };

        Cursor c = db.query(
                Episodes.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while(c.moveToNext()) {
            EpisodeInfo episodeInfo = new EpisodeInfo();
            episodeInfo.setEpisodeType(c.getInt(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_TYPE)));
            episodeInfo.setShowName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_NAME)));
            episodeInfo.setShowID(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_ID)));
            episodeInfo.setSeason(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SEASON)));
            episodeInfo.setEpisodeNum(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NUMBER)));
            episodeInfo.setEpisodeName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NAME)));
            episodeInfo.setPubdate(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_PUBDATE)));


            Date episodeDate = DateHelper.parseEpisodeDate(episodeInfo.getPubdate());

            //MyLog.d("read episode info " + episodeInfo.getShowName() + " " + episodeInfo.getEpisodeName() + " " + episodeInfo.getPubdate());
            if(DateHelper.isWithinThisWeek(episodeDate)){
                //MyLog.d("in database " + episodeInfo.getPubdate() + " add to this week " + episodeInfo.getShowName());
                thisWeekEpisodes.add(episodeInfo);
            }

        }

        c.close();
        return thisWeekEpisodes;
    }

    public void deleteAllEpisodes() {
        // Not such truncate sql command in sqlite
        db.execSQL(AizhuijuDBHelper.SQL_DELETE_EPISODES);
        db.execSQL(AizhuijuDBHelper.SQL_CREATE_EPISODES);
    }

    public int deleteEpisodeInfoByName(String showName) {
        return db.delete(Episodes.TABLE_NAME, Episodes.COLUMN_NAME_SHOW_NAME + "= '" + showName + "'", null);
    }

    public ArrayList<EpisodeInfo> getReturnEpisodes() {
        ArrayList<EpisodeInfo> returnEpisodes = new ArrayList<EpisodeInfo>();
        String[] projection = {
                Episodes.COLUMN_NAME_SHOW_ID,
                Episodes.COLUMN_NAME_UPDATED_AT,
                Episodes.COLUMN_NAME_SHOW_NAME,
                Episodes.COLUMN_NAME_SEASON,
                Episodes.COLUMN_NAME_PUBDATE,
                Episodes.COLUMN_NAME_EPISODE_NUMBER,
                Episodes.COLUMN_NAME_EPISODE_NAME,
                Episodes.COLUMN_NAME_EPISODE_TYPE

        };

        Cursor c = db.query(
                Episodes.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                Episodes.COLUMN_NAME_EPISODE_TYPE + "=" + EpisodeInfo.NEXT_EPISODE_TYPE,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );


        while(c.moveToNext()) {
            EpisodeInfo episodeInfo = new EpisodeInfo();
            episodeInfo.setEpisodeType(c.getInt(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_TYPE)));
            episodeInfo.setShowName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_NAME)));
            episodeInfo.setShowID(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SHOW_ID)));
            episodeInfo.setSeason(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_SEASON)));
            episodeInfo.setEpisodeNum(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NUMBER)));
            episodeInfo.setEpisodeName(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_EPISODE_NAME)));
            episodeInfo.setPubdate(c.getString(c.getColumnIndex(Episodes.COLUMN_NAME_PUBDATE)));


            Date episodeDate = DateHelper.parseEpisodeDate(episodeInfo.getPubdate());

            //MyLog.d("read episode info " + episodeInfo.getShowName() + " " + episodeInfo.getEpisodeName() + " " + episodeInfo.getPubdate());
            if(DateHelper.isLargerThanToday(episodeDate)){
                //MyLog.d("in database " + episodeInfo.getPubdate() + " add to this week " + episodeInfo.getShowName());
                returnEpisodes.add(episodeInfo);
            }

        }

        c.close();
        return returnEpisodes;
    }



}
