package com.tomking.aizhuiju.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.common.BilibiliAPI;
import com.bilibili.models.EpisodeVideoHolder;
import com.bilibili.models.SearchResultItem;
import com.douban.models.Movie;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.common.DateHelper;
import com.tomking.aizhuiju.common.ImageLoader;
import com.tomking.aizhuiju.models.DayEpisodes;
import com.tomking.aizhuiju.models.EpisodeGroup;
import com.tomking.aizhuiju.service.DatabaseService;
import com.tomking.aizhuiju.test.MyLog;
import com.tomking.aizhuiju.webservice.CommandBuilder;
import com.tomking.aizhuiju.webservice.WorkerThread;
import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by xtang on 14-4-14.
 */
public class LatestEpisodesFragment extends Fragment  {
    private DatabaseService dbService;
    private LatestShowsHandler handler = new LatestShowsHandler();
    private LatestEpisodesListAdapter mAdapter;
    private MainPagerAdapter.ServiceHolder serviceHolder;
    private ProgressBar progressBar;

    public LatestEpisodesFragment(MainPagerAdapter.ServiceHolder serviceHolder) {
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
        progressBar = (ProgressBar) rootView.findViewById(R.id.update_progress);
        mAdapter = new LatestEpisodesListAdapter(getActivity());
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
            MyLog.d("fragment visible");
            dbService = serviceHolder.getDbService();
            if(dbService != null)
                dbService.getLatestEpisodeInfos(handler);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        dbService = serviceHolder.getDbService();
        if(dbService != null)
            dbService.getLatestEpisodeInfos(handler);

    }



    private class LatestEpisodesListAdapter extends BaseAdapter {

        private ArrayList<EpisodeGroup> groupedEpisodes;
        private LayoutInflater mInflater;
        private Context context;
        //public HashMap<String, String> episodeURL = new HashMap<String, String>();
        public HashMap<String, SearchResultItem> foundEpisodes = new HashMap<String, SearchResultItem>();
        public HashMap<String, View> urlObserver = new HashMap<String, View>();
        private Boolean isUpdating = false;

        public LatestEpisodesListAdapter(Context context) {
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.groupedEpisodes = new ArrayList<EpisodeGroup>();
        }

        public int getEpisodesNumber() {
            int count = 0;
            for(EpisodeGroup group : groupedEpisodes) {
                for(EpisodeInfo episode : group.getEpisodes()) {
                    count ++;
                }
            }
            return count;
        }

        public void setDayEpisodes(ArrayList<EpisodeGroup> received) {
            this.groupedEpisodes.clear();
            HashMap<String, SearchResultItem> temp = new HashMap<String, SearchResultItem>();
            for(EpisodeGroup group : received){
                this.groupedEpisodes.add(group);
                for(EpisodeInfo episodeInfo : group.getEpisodes()) {
                    if(foundEpisodes.containsKey(episodeInfo.getEpisodeID())) {
                        temp.put(episodeInfo.getEpisodeID(), foundEpisodes.get(episodeInfo.getEpisodeID()));
                    }
                }

            }
            foundEpisodes = temp;

        }

        public void setIsUpdating(boolean val) {
            this.isUpdating = val;
        }

        public boolean isUpdating(){
            return this.isUpdating;
        }

        @Override
        public int getCount() {
            return groupedEpisodes.size();
        }

        @Override
        public Object getItem(int i) {
            return groupedEpisodes.get(i);
        }


        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LinearLayout rootView = (LinearLayout) mInflater.inflate(R.layout.item_day_episodes, null);
            EpisodeGroup episodeGroup = groupedEpisodes.get(position);

            ArrayList<EpisodeInfo> episodes = episodeGroup.getEpisodes();


            TextView dayTitle = (TextView)rootView.findViewById(R.id.text_day);
            dayTitle.setText(episodeGroup.getTitle());

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
                urlObserver.put(episode.getEpisodeID(), episode_item);

                /*
                if(episodeURL.containsKey(episode.getEpisodeID())) {
                    String url = episodeURL.get(episode.getEpisodeID());
                    if(url == null) {
                        episode_video.setText("未更新");
                    } else {
                        episode_item.setTag(url);
                        episode_video.setText("已更新");
                    }
                    episode_video.setVisibility(View.VISIBLE);
                }*/
                updateEpisodeItem(episode.getEpisodeID());

                episode_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(context, (String)view.getTag(), Toast.LENGTH_SHORT).show();
                        SearchResultItem searchResultItem = (SearchResultItem) view.getTag();
                        if(searchResultItem != null) {
                            new OpenBrowserDialogFragment(searchResultItem).show(getFragmentManager(), "open link");
                            //openWebPage(BilibiliAPI.getBilibiliURL(searchResultItem.getID()));
                        }
                    }
                });

                rootView.addView(episode_item, i+1, params);
            }
            return rootView;


        }

        public void updateEpisodeVideo() {
            int count = 0;
            progressBar.setVisibility(View.VISIBLE);
            /*
            progressBar.setIndeterminate(true);
            progressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.apptheme_progress_indeterminate_horizontal_holo_light));
            */
            isUpdating = true;

            for(EpisodeGroup group : groupedEpisodes) {
                for(EpisodeInfo episode : group.getEpisodes()) {
                    Movie show = dbService.getCurrentShowByID(episode.getShowID());
                    if(!foundEpisodes.containsKey(episode.getEpisodeID())) {
                        try{
                            new WorkerThread(new CommandBuilder.FindEpisodeVideo(
                                    new String[]{show.getAltTitle(), show.getTitle()},
                                    episode
                            ), handler, WorkerThread.DELAY_UNIT * (count++)).start();

                            /*
                            new WorkerThread(new CommandBuilder.FindEpisodeVideo(
                                    show.getTitle(),
                                    episode
                            ), handler, WorkerThread.DELAY_UNIT * (count++)).start();
                            */
                        } catch(Exception e) {
                            MyLog.d("Exception in WeekShows "+ e.toString());

                        }

                    }

                }
            }
        }

        public void updateEpisodeItem(String episodeID) {
            View item = urlObserver.get(episodeID);
            //String url = episodeURL.get(episodeID);

            if(item == null || foundEpisodes.get(episodeID) == null)
                return;

            String url = foundEpisodes.get(episodeID).getID();
            TextView episode_video = (TextView)item.findViewById(R.id.episode_video);
            if(url != null && !url.equals("")){
                MyLog.d("set video textview");
                episode_video.setText("已更新");
                item.setTag(foundEpisodes.get(episodeID));
                item.invalidate();
            } else {
                episode_video.setText("未更新");
                item.invalidate();
            }
            episode_video.setVisibility(View.VISIBLE);
        }

        public void openWebPage(String url) {
            Uri webpage = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        private class OpenBrowserDialogFragment extends DialogFragment {

            private SearchResultItem item;

            public OpenBrowserDialogFragment(SearchResultItem item) {
                this.item = item;
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the Builder class for convenient dialog construction
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String openBrowser = context.getResources().getString(R.string.open_browser);
                builder.setMessage(openBrowser + item.getTitle())
                        .setPositiveButton(R.string.acknowledge, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // FIRE ZE MISSILES!
                                openWebPage(BilibiliAPI.getBilibiliURL(item.getID()));
                                OpenBrowserDialogFragment.this.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                OpenBrowserDialogFragment.this.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
        }
    }

    private class LatestShowsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommandBuilder.GET_LATEST_EPISODE_INFO:
                    MyLog.d("received day episodes");
                    ArrayList<EpisodeGroup> result = (ArrayList<EpisodeGroup>) msg.obj;

                    mAdapter.setDayEpisodes(result);
                    if(mAdapter.foundEpisodes.size() < mAdapter.getEpisodesNumber()) {
                        MyLog.d("show progress bar");
                        progressBar.setVisibility(View.VISIBLE);
                        /*
                        progressBar.setIndeterminate(true);
                        progressBar.setIndeterminateDrawable(getActivity().getResources().getDrawable(R.drawable.apptheme_progress_indeterminate_horizontal_holo_light));
                        */
                        mAdapter.setIsUpdating(false);
                    } else {
                        mAdapter.setIsUpdating(true);
                        progressBar.setVisibility(View.GONE);
                    }
                    if(!mAdapter.isUpdating())
                        mAdapter.updateEpisodeVideo();
                    mAdapter.notifyDataSetChanged();
                    break;

                case CommandBuilder.FIND_EPISODE_VIDEO:
                    //progressBar.setVisibility(View.VISIBLE);
                    EpisodeVideoHolder holder = (EpisodeVideoHolder) msg.obj;

                    if(mAdapter.foundEpisodes.containsKey(holder.getID())) {
                        if(holder.getEpisodeAVs().size() > 0 && mAdapter.foundEpisodes.get(holder.getID()) == null) {
                            //mAdapter.episodeURL.put(holder.getID(), holder.getEpisodeAVs().get(0));
                            mAdapter.foundEpisodes.put(holder.getID(), holder.getEpisodeAVs().get(0));
                            mAdapter.updateEpisodeItem(holder.getID());
                            //mAdapter.notifyDataSetChanged();
                        }

                    } else {
                        if(holder.getEpisodeAVs().size() > 0) {
                            //mAdapter.episodeURL.put(holder.getID(), holder.getEpisodeAVs().get(0));
                            mAdapter.foundEpisodes.put(holder.getID(), holder.getEpisodeAVs().get(0));
                            mAdapter.updateEpisodeItem(holder.getID());
                            //mAdapter.notifyDataSetChanged();
                        } else {
                            //mAdapter.episodeURL.put(holder.getID(), null);
                            mAdapter.foundEpisodes.put(holder.getID(), null);
                            mAdapter.updateEpisodeItem(holder.getID());
                            //mAdapter.notifyDataSetChanged();
                        }
                    }
                    if(mAdapter.foundEpisodes.size() == mAdapter.getEpisodesNumber()) {
                        progressBar.setVisibility(View.GONE);
                        mAdapter.setIsUpdating(false);
                    }
                    break;

            }
        }
    }
}
