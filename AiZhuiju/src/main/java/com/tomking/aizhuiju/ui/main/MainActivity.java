package com.tomking.aizhuiju.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.douban.models.MovieAPI;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.dao.AizhuijuDBHelper;
import com.tomking.aizhuiju.models.DayEpisodes;
import com.tomking.aizhuiju.service.DatabaseService;
import com.tomking.aizhuiju.test.MyLog;
import com.tomking.aizhuiju.ui.search.SearchActivity;
import com.tomking.aizhuiju.webservice.CommandBuilder;
import com.tomking.aizhuiju.webservice.WorkerThread;
import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {

    private MovieAPI movieAPI = MovieAPI.getInstance();

    private ViewPager mainViewPager;
    private DatabaseService mService;
    private boolean mBound;
    private MainPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind to DatabaseService
        bindService(new Intent(this, DatabaseService.class), mConnection,
                Context.BIND_AUTO_CREATE);


        mainViewPager = (ViewPager)findViewById(R.id.main_viewpager);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(pagerAdapter);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            DatabaseService.DatabaseServiceBinder binder = (DatabaseService.DatabaseServiceBinder) service;
            mService = binder.getService();
            pagerAdapter.setService(mService);

            pagerAdapter.notifyDataSetChanged();

            mService.updateAllEpisodeInfos();
            //mService.getThisWeekEpisodeInfos(testHandler);
            mBound = true;

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommandBuilder.GET_THIS_WEEK_EPISODE_INFO:
                    ArrayList<DayEpisodes> result = (ArrayList<DayEpisodes>) msg.obj;
                    break;
            }
        }
    };

}
