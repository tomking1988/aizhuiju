package com.tomking.aizhuiju.ui.main;

import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bilibili.models.EpisodeVideoHolder;
import com.douban.models.Movie;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.common.DateHelper;
import com.tomking.aizhuiju.common.ImageLoader;
import com.tomking.aizhuiju.dao.DatabaseContract;
import com.tomking.aizhuiju.models.DayEpisodes;
import com.tomking.aizhuiju.service.DatabaseService;
import com.tomking.aizhuiju.test.MyLog;
import com.tomking.aizhuiju.webservice.*;
import com.tomking.aizhuiju.webservice.WorkerThread;
import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by xtang on 14-4-10.
 */
public class WeekShowsFragment extends Fragment {

    private DatabaseService dbService;
    private WeekShowsHandler handler = new WeekShowsHandler();
    private WeekEpisodesListAdapter mAdapter;
    private MainPagerAdapter.ServiceHolder serviceHolder;

    public WeekShowsFragment(MainPagerAdapter.ServiceHolder serviceHolder) {
        this.serviceHolder = serviceHolder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        LinearLayout rootView = (LinearLayout)inflater.inflate(
                R.layout.fragment_week_episodes, container, false);

        ListView weekEpisodesList = (ListView) rootView.findViewById(R.id.list_week_episodes);
        mAdapter = new WeekEpisodesListAdapter(getActivity());
        weekEpisodesList.setAdapter(mAdapter);

        if(dbService != null)
            dbService.getThisWeekEpisodeInfos(handler);
        else
            MyLog.d("db service empty");

        return rootView;
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            MyLog.d("fragment resume");
            dbService = serviceHolder.getDbService();
            if(dbService != null)
                dbService.getThisWeekEpisodeInfos(handler);
        }
    }

    private class WeekEpisodesListAdapter extends BaseAdapter {

        private ArrayList<DayEpisodes> dayEpisodes;
        private LayoutInflater mInflater;
        private Context context;
        public HashMap<String, String> episodeURL = new HashMap<String, String>();

        public WeekEpisodesListAdapter(Context context) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.dayEpisodes = new ArrayList<DayEpisodes>();
        }

        public void setDayEpisodes(ArrayList<DayEpisodes> received) {
            dayEpisodes.clear();
            for(DayEpisodes episodes : received) {
                dayEpisodes.add(episodes);
            }


        }

        @Override
        public int getCount() {
            return dayEpisodes.size();
        }

        @Override
        public Object getItem(int i) {
            return dayEpisodes.get(i);
        }


        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {

            LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_day_episodes, null);
            DayEpisodes dayEpisode = dayEpisodes.get(position);
            int day = dayEpisode.getDay();
            ArrayList<EpisodeInfo> episodes = dayEpisode.getEpisodes();

            TextView dayTitle = (TextView)rootView.findViewById(R.id.text_day);
            dayTitle.setText(DateHelper.DAY_NAME[day-1]);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


            for(int i=0; i<episodes.size(); i++) {
                EpisodeInfo episode = episodes.get(i);
                RelativeLayout episode_item = (RelativeLayout) mInflater.inflate(R.layout.item_episode, null);

                TextView episode_number = (TextView) episode_item.findViewById(R.id.episode_number);
                TextView episode_name = (TextView) episode_item.findViewById(R.id.episode_name);
                TextView episode_pubdate = (TextView) episode_item.findViewById(R.id.pubdate);
                TextView episode_video = (TextView) episode_item.findViewById(R.id.episode_video);
                ImageView episode_image = (ImageView) episode_item.findViewById(R.id.show_image);

                episode_number.setText(episode.getSeason() + "x" + episode.getEpisodeNum() + " | " + episode.getShowName());
                episode_name.setText(episode.getEpisodeName());
                episode_pubdate.setText(episode.getPubdate());

                Movie show = dbService.getCurrentShowByID(episode.getShowID());
                episode_image.setTag(R.id.IMAGE_ID, show.getImage());
                ImageLoader imageLoader = new ImageLoader(episode_image, context);
                imageLoader.execute(show.getImage());
                MyLog.d("load image" + show.getTitle());

                if(!episodeURL.containsKey(episode.getEpisodeID())) {
                    try{
                        new WorkerThread(new CommandBuilder.FindEpisodeVideo(
                                new String[]{show.getAltTitle(), show.getTitle()},
                                episode
                        ), handler).start();

                        /*
                        new WorkerThread(new CommandBuilder.FindEpisodeVideo(
                                show.getTitle(),
                                episode
                        ), handler).start();
                        */
                    } catch(Exception e) {
                        MyLog.d("Exception in WeekShows "+ e.toString());
                    }

                } else {
                    String url = episodeURL.get(episode.getEpisodeID());
                    if(url == null) {
                        episode_video.setText("未更新");
                    } else {
                        episode_video.setText("已更新");
                    }
                    episode_video.setVisibility(View.VISIBLE);
                }

                rootView.addView(episode_item, i+1, params);
            }
            return rootView;


        }
    }


    private class WeekShowsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommandBuilder.GET_THIS_WEEK_EPISODE_INFO:
                    MyLog.d("received day episodes");
                    ArrayList<DayEpisodes> result = (ArrayList<DayEpisodes>) msg.obj;
                    mAdapter.setDayEpisodes(result);
                    mAdapter.notifyDataSetChanged();
                    break;

                case CommandBuilder.FIND_EPISODE_VIDEO:
                    EpisodeVideoHolder holder = (EpisodeVideoHolder) msg.obj;
                    if(mAdapter.episodeURL.containsKey(holder.getID())) {
                        if(holder.getEpisodeAVs().size() > 0) {
                            mAdapter.episodeURL.put(holder.getID(), holder.getEpisodeAVs().get(0).getID());
                        }
                    } else {
                        if(holder.getEpisodeAVs().size() > 0) {
                            mAdapter.episodeURL.put(holder.getID(), holder.getEpisodeAVs().get(0).getID());
                        } else {
                            mAdapter.episodeURL.put(holder.getID(), null);
                        }
                    }
                    break;

            }
        }
    }
}
