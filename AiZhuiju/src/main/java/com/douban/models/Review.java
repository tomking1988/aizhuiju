package com.douban.models;

public class Review extends Bean{

   protected long id;
	protected String title;
	protected String alt;
	protected int votes;
	
	public Review(long id, String title, String alt, int votes){
		this.id = id;
		this.title = title;
		this.alt = alt;
		this.votes = votes;
	}
}
