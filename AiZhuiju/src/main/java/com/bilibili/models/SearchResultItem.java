package com.bilibili.models;

public class SearchResultItem {
	private String type;
	private String lid;
	private String aid;
	private String spid;
	private String id;
	private String author;
	private String play;
	private String review;
	private String video_review;
	private String favorites;
	private String title;
	private String description;
	private String tag;
	private String pic;
	
	public String getID() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}

    public boolean isVideo() {
        if(type == null)
            return false;
        return type.equals("video");
    }
}
