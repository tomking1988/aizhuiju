package com.tomking.aizhuiju.webservice;

import android.os.Handler;

import com.bilibili.common.BilibiliAPI;
import com.bilibili.models.EpisodeVideoHolder;
import com.bilibili.models.SearchResultItem;
import com.douban.models.Movie;
import com.douban.models.MovieAPI;
import com.douban.models.MovieSearchResult;
import com.tomking.aizhuiju.common.DateHelper;
import com.tomking.aizhuiju.dao.AizhuijuDBManager;
import com.tomking.aizhuiju.models.DayEpisodes;
import com.tomking.aizhuiju.models.EpisodeGroup;
import com.tomking.aizhuiju.test.MyLog;
import com.tvrage.common.TvRageAPI;
import com.tvrage.models.EpisodeInfo;
import com.tvrage.models.ShowStatus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by xtang on 14-4-2.
 */
public class CommandBuilder {

    public static final int GET_MOVIE_BY_ID = 1;
    public static final int SEARCH_MOVIE = 2;
    public static final int ADD_CURRENT_SHOW = 3;
    public static final int UPDATE_EPISODE_INFO = 4;
    //public static final int ADD_CURRENT_SHOW_BY_ID = 4;
    public static final int UPDATE_ALL_EPISODE_INFO = 5;
    public static final int GET_LATEST_EPISODE_INFO = 6;
    public static final int GET_NEXT_EPISODE_INFO = 7;
    public static final int GET_THIS_WEEK_EPISODE_INFO = 8;
    public static final int GET_RETURN_EPISODES = 9;
    public static final int FIND_EPISODE_VIDEO = 10;

    private static final int RETRY_NUMBER = 5;


    public static GetMovieByID getMovieByID(long id) {
        return new GetMovieByID(id);
    }

    public static SearchMovie getSearchMovie(String query, String tag) {
        return new SearchMovie(query, tag);
    }

    public static class UpdateEpisodeInfo extends Command {

        private Movie show;
        private AizhuijuDBManager dbManager;
        private int retry = RETRY_NUMBER;

        public UpdateEpisodeInfo(Movie show, AizhuijuDBManager dbManager) {
            super(UPDATE_EPISODE_INFO);
            this.show = show;
            this.dbManager = dbManager;

        }

        @Override
        public Object execute() {
            //MyLog.d("update episode info " + show.getTitle());
            ShowStatus showStatus = null;

            try{
                showStatus = TvRageAPI.getShowStatusByName(show.getTitle());
            } catch(Exception e) {

            }

            if(showStatus == null && this.retry >= 0) {
                this.retry --;
                MyLog.d("retry");
                new WorkerThread(this, null).start();
                return null;
            }

            if(showStatus == null) {
                return null;
            }

            EpisodeInfo latestEpisode = showStatus.getLatestEpisode();
            EpisodeInfo nextEpisode = showStatus.getNextEpisode();


            if(latestEpisode != null) {
                MyLog.d("this week episode info " + latestEpisode.getEpisodeName() + " " + latestEpisode.getPubdate());

                latestEpisode.setShowID(show.getIdLong());
                latestEpisode.setShowName(show.getTitle());
                dbManager.connect();
                dbManager.addEpisodeInfo(latestEpisode);
                //dbManager.close();
            }

            if(nextEpisode != null) {
                nextEpisode.setShowID(show.getIdLong());
                nextEpisode.setShowName(show.getTitle());
                dbManager.connect();
                dbManager.addEpisodeInfo(nextEpisode);
                //dbManager.close();
            }


            return null;
        }
    }

    public static class AddCurrentShow extends Command {

        private long id;
        private String imageURL;

        public AddCurrentShow (long id) {
            super(ADD_CURRENT_SHOW);
            this.id = id;

        }

        public AddCurrentShow (long id, String imageURL) {
            super(ADD_CURRENT_SHOW);
            this.id = id;
            this.imageURL = imageURL;
        }

        @Override
        public Movie execute() {
            Movie show = MovieAPI.getInstance().byId(id);
            if(this.imageURL != null) {
                show.setImage(imageURL);
            }
            return show;
        }


    }



    public static class GetMovieByID extends Command {

        private long id;


        public GetMovieByID(long id) {
            super(GET_MOVIE_BY_ID);
            this.id = id;

        }

        @Override
        public Movie execute() {
            return MovieAPI.getInstance().byId(id);
        }


    }

    public static class SearchMovie extends Command {

        private String query;
        private String tag;


        public SearchMovie(String query, String tag) {
            super(SEARCH_MOVIE);
            this.query = query;
            this.tag = tag;

        }

        @Override
        public MovieSearchResult execute() {
            return MovieAPI.getInstance().search(query, tag);

        }

    }

    public static class UpdateAllEpisodeInfo extends Command {

        private ArrayList<Movie> currentShows;
        private AizhuijuDBManager dbManager;

        public UpdateAllEpisodeInfo(AizhuijuDBManager dbManager) {
            super(UPDATE_ALL_EPISODE_INFO);
            this.dbManager = dbManager;

        }

        @Override
        public Object execute() {
            dbManager.connect();
            if(dbManager.isUpdatedThisWeekEpisodes()) {
                return null;
            }
            dbManager.deleteAllEpisodes();
            currentShows = dbManager.getCurrentShows();
            for(Movie show : currentShows) {
                new WorkerThread(new UpdateEpisodeInfo(show, dbManager), null).start();
            }
            //dbManager.close();
            return null;
        }
    }

    public static class GetThisWeekEpisodeInfos extends Command {

        private AizhuijuDBManager dbManager;

        public GetThisWeekEpisodeInfos(AizhuijuDBManager dbManager) {
            super(GET_THIS_WEEK_EPISODE_INFO);
            this.dbManager = dbManager;

        }

        @Override
        public ArrayList<DayEpisodes> execute() {
            dbManager.connect();
            ArrayList<EpisodeInfo> episodeInfos = dbManager.getThisWeekEpisodes();
            //dbManager.close();
            ArrayList<DayEpisodes> results = new ArrayList<DayEpisodes>();
            HashMap<Integer, ArrayList<EpisodeInfo>> groupedEpisodes = new HashMap<Integer, ArrayList<EpisodeInfo>>();
            for(EpisodeInfo episodeInfo : episodeInfos) {

                //Date.getDay to Calendar day
                int day = DateHelper.parseEpisodeDate(episodeInfo.getPubdate()).getDay() + 1;
                MyLog.d("has episode " + episodeInfo.getShowName() + " " + day);
                if(groupedEpisodes.get(day) == null) {
                    ArrayList<EpisodeInfo> dayEpisodes = new ArrayList<EpisodeInfo>();
                    dayEpisodes.add(episodeInfo);
                    groupedEpisodes.put(day, dayEpisodes);
                } else {
                    groupedEpisodes.get(day).add(episodeInfo);
                }
            }

            for(int i= 0; i<DateHelper.DAYS.length; i++) {
                if(groupedEpisodes.containsKey(DateHelper.DAYS[i])) {
                    results.add(new DayEpisodes(DateHelper.DAYS[i], groupedEpisodes.get(DateHelper.DAYS[i])));
                }
            }
            return results;
        }
    }

    public static class GetReturnEpisodes extends Command {

        private AizhuijuDBManager dbManager;

        public GetReturnEpisodes(AizhuijuDBManager dbManager) {
            super(GET_RETURN_EPISODES);
            this.dbManager = dbManager;

        }

        @Override
        public ArrayList<EpisodeInfo> execute() {
            dbManager.connect();
            ArrayList<EpisodeInfo> episodeInfos = dbManager.getReturnEpisodes();
            Collections.sort(episodeInfos);
            return episodeInfos;
        }
    }

    public static class GetLatestEpisodes extends Command {

        private AizhuijuDBManager dbManager;

        public GetLatestEpisodes(AizhuijuDBManager dbManager) {
            super(GET_LATEST_EPISODE_INFO);
            this.dbManager = dbManager;

        }

        @Override
        public ArrayList<EpisodeGroup> execute() {
            dbManager.connect();
            ArrayList<EpisodeInfo> episodeInfos = dbManager.getAllEpisodes();
            Collections.sort(episodeInfos);
            HashMap<String, ArrayList<EpisodeInfo>> groupedEpisodes = new HashMap<String, ArrayList<EpisodeInfo>>();
            groupedEpisodes.put(DateHelper.THIS_WEEK, new ArrayList<EpisodeInfo>());
            groupedEpisodes.put(DateHelper.LAST_WEEK, new ArrayList<EpisodeInfo>());
            groupedEpisodes.put(DateHelper.LONG_BEFORE, new ArrayList<EpisodeInfo>());

            for(EpisodeInfo episode : episodeInfos) {
                if(DateHelper.isWithinThisWeek(DateHelper.parseEpisodeDate(episode.getPubdate()))) {
                    groupedEpisodes.get(DateHelper.THIS_WEEK).add(episode);
                    continue;
                }

                if(DateHelper.isWithinLastWeek(DateHelper.parseEpisodeDate(episode.getPubdate()))) {
                    groupedEpisodes.get(DateHelper.LAST_WEEK).add(episode);
                    continue;
                }

                if(DateHelper.isLongBefore(DateHelper.parseEpisodeDate(episode.getPubdate()))) {
                    groupedEpisodes.get(DateHelper.LONG_BEFORE).add(episode);
                }

            }

            ArrayList<EpisodeGroup> result = new ArrayList<EpisodeGroup>();
            if(groupedEpisodes.get(DateHelper.THIS_WEEK).size() > 0) {

                EpisodeGroup group = new EpisodeGroup(DateHelper.THIS_WEEK, groupedEpisodes.get(DateHelper.THIS_WEEK));
                result.add(group);
            }

            if(groupedEpisodes.get(DateHelper.LAST_WEEK).size() > 0) {

                EpisodeGroup group = new EpisodeGroup(DateHelper.LAST_WEEK, groupedEpisodes.get(DateHelper.LAST_WEEK));
                result.add(group);
            }

            if(groupedEpisodes.get(DateHelper.LONG_BEFORE).size() > 0) {
                EpisodeGroup group = new EpisodeGroup(DateHelper.LONG_BEFORE, groupedEpisodes.get(DateHelper.LONG_BEFORE));
                result.add(group);
            }

            return result;
        }
    }

    public static class FindEpisodeVideo extends Command {

        private String[] names;
        private int season;
        private int episode;
        private String id;
        private int retry = RETRY_NUMBER;
        private String episodeID ;

    /*
        public FindEpisodeVideo(String id, String name, int season, int episode) {
            super(FIND_EPISODE_VIDEO);
            this.name = name;
            this.season = season;
            this.episode = episode;
            this.id = id;
        }
        */

        public FindEpisodeVideo(String[] names, EpisodeInfo episodeInfo) {
            super(FIND_EPISODE_VIDEO);
            this.names = names;
            this.season = Integer.parseInt(episodeInfo.getSeason());
            this.episode = Integer.parseInt(episodeInfo.getEpisodeNum());
            this.id = episodeInfo.getEpisodeID();
        }

        @Override
        public EpisodeVideoHolder execute() {
            ArrayList<SearchResultItem> episodeAVs = null;
            for(String name: names) {
                episodeAVs = BilibiliAPI.getInstance().findEpisode(name, season, episode);
                   for(SearchResultItem av : episodeAVs) {
                    MyLog.d("found video " + av.getTitle() + " " + name + " " + season + " " + episode);
                }
                if(episodeAVs != null && episodeAVs.size() > 0)
                    break;
            }


            return  new EpisodeVideoHolder(this.id, episodeAVs);
        }


    }
}
