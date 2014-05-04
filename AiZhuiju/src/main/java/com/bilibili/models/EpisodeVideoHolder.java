package com.bilibili.models;

import java.util.ArrayList;

/**
 * Created by xtang on 14-4-14.
 */
public class EpisodeVideoHolder {
    private String id;
    private ArrayList<SearchResultItem> episodeAVs;

    public EpisodeVideoHolder(String id, ArrayList<SearchResultItem> episodeAVs) {
        this.id = id;
        this.episodeAVs = episodeAVs;
    }

    public String getID() {
        return this.id;
    }

    public ArrayList<SearchResultItem> getEpisodeAVs() {
        return this.episodeAVs;
    }
}
