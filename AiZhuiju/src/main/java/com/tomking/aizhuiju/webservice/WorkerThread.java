package com.tomking.aizhuiju.webservice;

import android.os.Handler;
import android.os.Message;

import com.douban.models.MovieSearchResult;
import com.tomking.aizhuiju.test.MyLog;

/**
 * Created by xtang on 14-4-2.
 */
public class WorkerThread extends Thread {
    private Command command;
    private Handler handler;
    private long delay;
    public static final int DELAY_UNIT = 6 * 1000;

    public WorkerThread(Command command, Handler handler) {
        this.command = command;
        this.handler = handler;
    }

    public WorkerThread(Command command, Handler handler, long delay) {
        this.command = command;
        this.handler = handler;
        this.delay = delay;
    }

    @Override
    public void run() {
        try{
            if(delay > 0){
                sleep(delay);
            }
        }catch(Exception e) {
            MyLog.d("Exception in WorkerThread " + e.toString());
        }


        if(handler == null) {
            command.execute();
            return;
        }

        Message msg = handler.obtainMessage();
        msg.what = command.getCommandType();
        msg.obj = command.execute();

        if(msg.obj == null) {
            msg.recycle();
        } else {
            handler.sendMessage(msg);
        }


    }
}
