package com.tomking.aizhuiju.ui.search;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.douban.models.MovieSearchResult;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.service.DatabaseService;
import com.tomking.aizhuiju.webservice.CommandBuilder;
import com.tomking.aizhuiju.webservice.WorkerThread;
import com.tomking.aizhuiju.service.DatabaseService.DatabaseServiceBinder;

public class SearchActivity extends ActionBarActivity {

    private ListView searchResultList;
    private SearchListAdapter searchListAdapter;
    private ImageButton searchButton;
    private EditText searchEdittext;
    private Handler mHandler = new SearchHandler();
    private TextView searchNotFound;
    private DatabaseService mService;
    private boolean mBound;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            DatabaseServiceBinder binder = (DatabaseServiceBinder) service;
            mService = binder.getService();
            searchListAdapter.setDBService(mService);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Bind to DatabaseService
        bindService(new Intent(this, DatabaseService.class), mConnection,
                Context.BIND_AUTO_CREATE);


        searchNotFound = (TextView) findViewById(R.id.search_notfound);
        searchResultList = (ListView) findViewById(R.id.search_result_list);
        searchListAdapter = new SearchListAdapter(getApplicationContext(), new MovieSearchResult(0, 0, 0, null), searchResultList);
        searchResultList.setAdapter(searchListAdapter);
        searchEdittext = (EditText) findViewById(R.id.search_edittext);
        searchEdittext.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    String query = searchEdittext.getText().toString();
                    new WorkerThread(CommandBuilder.getSearchMovie(query, ""), mHandler).start();
                    return true;
                }
                return false;
            }
        });
        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String query = searchEdittext.getText().toString();
                new WorkerThread(CommandBuilder.getSearchMovie(query, ""), mHandler).start();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchEdittext.getWindowToken(), 0);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    private class SearchHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CommandBuilder.SEARCH_MOVIE :
                    MovieSearchResult searchResult = (MovieSearchResult) msg.obj;
                    if(searchResult.getSubjects().length > 0) {
                        searchListAdapter.setSearchResult(searchResult);
                        searchListAdapter.notifyDataSetChanged();
                        searchNotFound.setVisibility(View.GONE);
                        searchResultList.setVisibility(View.VISIBLE);
                    } else {
                        searchNotFound.setVisibility(View.VISIBLE);
                        searchResultList.setVisibility(View.GONE);
                    }

                    break;
            }

        }
    }

}
