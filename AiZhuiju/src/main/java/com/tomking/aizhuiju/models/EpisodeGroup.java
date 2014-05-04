package com.tomking.aizhuiju.models;

import com.tvrage.models.EpisodeInfo;

import java.util.ArrayList;

/**
 * Created by xtang on 14-4-14.
 */
public class EpisodeGroup {

    private String title;
    private ArrayList<EpisodeInfo> episodes;

    public EpisodeGroup(String title, ArrayList<EpisodeInfo>  episodes) {
        this.title = title;
        this.episodes = episodes;
    }

    public String getTitle() {
        return this.title;
    }

    public ArrayList<EpisodeInfo>  getEpisodes() {
        return this.episodes;
    }
}
