package com.douban.models;

public class ListResult extends Bean{
	 
	protected int start;
	protected int count;
	protected int total;
	
	public ListResult(int start, int count, int total) {
		this.start = start;
		this.count = count;
		this.total = total;
	}
}
