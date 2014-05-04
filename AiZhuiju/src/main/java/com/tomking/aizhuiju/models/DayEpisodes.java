package com.tomking.aizhuiju.models;

import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;

/**
 * Created by xtang on 14-4-10.
 */
public class DayEpisodes {
    private int day;
    private ArrayList<EpisodeInfo>  episodes;

    public DayEpisodes(int day, ArrayList<EpisodeInfo>  episodes) {
        this.day = day;
        this.episodes = episodes;
    }

    public int getDay() {
        return this.day;
    }

    public ArrayList<EpisodeInfo>  getEpisodes() {
        return this.episodes;
    }
}
