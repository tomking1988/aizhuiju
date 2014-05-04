package com.douban.models;

public class MovieSearchResult extends ListResult{

	private SimpleMovieSubject[] subjects;
	
	public MovieSearchResult(int start, int count, int total, SimpleMovieSubject[] movies) {
		super(start, count, total);
		this.subjects = movies;
	}
	
	public SimpleMovieSubject[] getSubjects() {
		return subjects;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getCount() {
		return count;
	}
	
	public int getTotal() {
		return total;
	}
}
