package com.tomking.aizhuiju.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.douban.models.Movie;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.dao.AizhuijuDBManager;
import com.tomking.aizhuiju.models.DayEpisodes;
import com.tomking.aizhuiju.webservice.WorkerThread;
import com.tomking.aizhuiju.webservice.CommandBuilder;
import com.tomking.aizhuiju.webservice.CommandBuilder.*;
import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;

/**
 * Created by xtang on 14-4-9.
 */
public class DatabaseService extends Service {

    private AizhuijuDBManager dbManager;
    private final IBinder mBinder = new DatabaseServiceBinder();
    private DBServiceHandler mHandler = new DBServiceHandler();
    private boolean isEpisodeInfoUpdated = false;

    class DBServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommandBuilder.ADD_CURRENT_SHOW:
                    Movie show = (Movie) msg.obj;
                    dbManager.connect();
                    if(dbManager.existInCurrentShow(show)){
                        Toast.makeText(getApplicationContext(), "已追" + show.getTitle(), Toast.LENGTH_SHORT).show();
                    } else {
                        dbManager.addCurrentShow(show);
                        updateEpisodeInfo(show);
                        Toast.makeText(getApplicationContext(), R.string.show_added, Toast.LENGTH_SHORT).show();
                    }
                    //dbManager.close();
                    break;


            }
        }
    }

    public void addCurrentShowByID(String id, String imageURL) {
        long idLong = Long.parseLong(id);
        new WorkerThread(new AddCurrentShow(idLong, imageURL), mHandler).start();
    }

    public Movie getCurrentShowByID(String id) {
        dbManager.connect();
        return dbManager.getCurrentShowByID(id);
    }

    public void updateAllEpisodeInfos() {
        dbManager.connect();

        /*
        if(dbManager.isUpdatedThisWeekEpisodes()) {
            //dbManager.close();
            return;
        }*/

        new WorkerThread(new UpdateAllEpisodeInfo(dbManager), null).start();
    }

    public void updateEpisodeInfo(Movie show) {
        new WorkerThread(new UpdateEpisodeInfo(show, dbManager), null).start();
    }

    public void getThisWeekEpisodeInfos(Handler receiver) {
        new WorkerThread(new GetThisWeekEpisodeInfos(dbManager), receiver).start();
    }

    public void getLatestEpisodeInfos(Handler receiver) {
        new WorkerThread(new GetLatestEpisodes(dbManager), receiver).start();
    }

    public void getReturnEpisodes(Handler receiver) {
        new WorkerThread(new GetReturnEpisodes(dbManager), receiver).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(dbManager == null) {
            dbManager = AizhuijuDBManager.getInstance(getApplicationContext());
        }
        return mBinder;
    }

    public class DatabaseServiceBinder extends Binder {

        public DatabaseService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DatabaseService.this;
        }
    }

}
