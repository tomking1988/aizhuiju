package com.douban.models;

public class Search extends Bean{

	private String q;
	private String tag;
	private long start;
	private int count;
	
	public Search(String q, String tag, long start, int count) {
		this.q = q;
		this.tag = tag;
		this.start = start;
		this.count = count;
	}

}
