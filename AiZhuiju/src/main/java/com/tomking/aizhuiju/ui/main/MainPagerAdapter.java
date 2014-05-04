package com.tomking.aizhuiju.ui.main;

import android.app.Service;
import android.content.ServiceConnection;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.tomking.aizhuiju.service.DatabaseService;

/**
 * Created by xtang on 14-4-2.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private static int page_count = 3;
    private static final String TAB1 = "在追";
    private static final String TAB2 = "最新";
    private static final String TAB3 = "回归";
    private DatabaseService mService;
    private ServiceHolder serviceHolder = new ServiceHolder();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setService(DatabaseService service) {
        this.mService = service;
        serviceHolder.setDbService(service);
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new CurrentShowsFragment();
            case 1:
                return new LatestEpisodesFragment(serviceHolder);
            case 2:
                return new ReturnEpisodesFragment(serviceHolder);
            default:
                return  null;
        }


    }

    @Override
    public int getCount() {
        return page_count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String tab = "";
        switch (position) {
            case 0: tab = TAB1;
                break;
            case 1: tab = TAB2;
                break;
            case 2: tab = TAB3;
            default:
                break;
        }

        return tab;
    }

    public class ServiceHolder{
        private DatabaseService dbService;

        public void setDbService (DatabaseService dbService) {
            this.dbService = dbService;
        }

        public DatabaseService getDbService () {
            return this.dbService;
        }
    }
}
