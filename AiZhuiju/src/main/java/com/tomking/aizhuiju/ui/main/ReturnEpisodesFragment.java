package com.tomking.aizhuiju.ui.main;

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
import android.widget.TextView;

import com.douban.models.Movie;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.common.DateHelper;
import com.tomking.aizhuiju.common.FontManager;
import com.tomking.aizhuiju.common.ImageLoader;
import com.tomking.aizhuiju.models.DayEpisodes;
import com.tomking.aizhuiju.service.DatabaseService;
import com.tomking.aizhuiju.test.MyLog;
import com.tomking.aizhuiju.webservice.CommandBuilder;
import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;

/**
 * Created by xtang on 14-4-12.
 */
public class ReturnEpisodesFragment extends Fragment {

    private DatabaseService dbService;
    private MainPagerAdapter.ServiceHolder serviceHolder;
    private ReturnEpisodesListAdapter mAdapter;
    private ReturnEpisodesHandler handler = new ReturnEpisodesHandler();

    public ReturnEpisodesFragment(MainPagerAdapter.ServiceHolder serviceHolder) {
        this.serviceHolder = serviceHolder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        LinearLayout rootView = (LinearLayout)inflater.inflate(
                R.layout.fragment_return_episodes, container, false);

        ListView returnEpisodesList = (ListView) rootView.findViewById(R.id.list_return_episodes);
        mAdapter = new ReturnEpisodesListAdapter(getActivity());
        returnEpisodesList.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            dbService = serviceHolder.getDbService();
            if(dbService != null)
                dbService.getReturnEpisodes(handler);
        }
    }


    private class ReturnEpisodesListAdapter extends BaseAdapter {

        private ArrayList<EpisodeInfo> returnEpisodes;
        private LayoutInflater mInflater;
        private Context context;

        public ReturnEpisodesListAdapter(Context context) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.returnEpisodes = new ArrayList<EpisodeInfo>();
        }

        public void setReturnEpisodes(ArrayList<EpisodeInfo> returnEpisodes) {
            this.returnEpisodes.clear();
            for(EpisodeInfo episode : returnEpisodes) {
                this.returnEpisodes.add(episode);
            }
        }

        @Override
        public int getCount() {
            return returnEpisodes.size();
        }

        @Override
        public Object getItem(int i) {
            return returnEpisodes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            EpisodeInfo episode = returnEpisodes.get(position);
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_episode, null);
                holder.episodeImage = (ImageView)convertView.findViewById(R.id.show_image);
                holder.episodeNumber = (TextView)convertView.findViewById(R.id.episode_number);
                holder.episodeName = (TextView)convertView.findViewById(R.id.episode_name);
                holder.episodePubdate = (TextView) convertView.findViewById(R.id.pubdate);

                holder.episodeNumber.setText(episode.getSeason() + "x" + episode.getEpisodeNum() + " | " + episode.getShowName());
                holder.episodeName.setText(episode.getEpisodeName());

                int returnDays = DateHelper.getDaysFromNow(DateHelper.parseEpisodeDate(episode.getPubdate()));
                holder.episodePubdate.setText(episode.getPubdate() + " " + returnDays + "天后");

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
                holder.episodeNumber.setText(episode.getSeason() + "x" + episode.getEpisodeNum() + " | " + episode.getShowName());
                holder.episodeName.setText(episode.getEpisodeName());

                int returnDays = DateHelper.getDaysFromNow(DateHelper.parseEpisodeDate(episode.getPubdate()));
                holder.episodePubdate.setText(episode.getPubdate() + " " + returnDays + "天后");
            }

            Movie show = dbService.getCurrentShowByID(episode.getShowID());
            holder.episodeImage.setTag(R.id.IMAGE_ID, show.getImage());
            if(show.getImage() != null) {
                ImageLoader imageLoader = new ImageLoader(holder.episodeImage, context);
                imageLoader.execute(show.getImage());
            }



            return convertView;
        }
    }

    private static class ViewHolder{
        ImageView episodeImage;
        TextView episodeNumber;
        TextView episodeName;
        TextView episodePubdate;
    }


    private class ReturnEpisodesHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommandBuilder.GET_RETURN_EPISODES:
                    //MyLog.d("received day episodes");
                    ArrayList<EpisodeInfo> result = (ArrayList<EpisodeInfo>) msg.obj;
                    mAdapter.setReturnEpisodes(result);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
