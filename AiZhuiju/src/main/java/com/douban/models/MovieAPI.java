package com.douban.models;


public class MovieAPI extends BookMovieMusicAPI<Movie, MovieSearchResult, MovieReview> {

	private static final MovieAPI api = new MovieAPI();
	
	private MovieAPI(){}
	
	public static final MovieAPI getInstance() {
		return api;
	}
	
	@Override
	public String getURLPrefix() {
		// TODO Auto-generated method stub
		return api_prefix + "movie/";
	}
	
	 
	public Movie byId(long id) {
		return  byId(id, Movie.class);
	}
	
	public MovieSearchResult search(String query, String tag, int page, int count) {
		return search(query, tag, page, count, MovieSearchResult.class);
	}

	public MovieSearchResult search(String query, String tag) {
		return search(query, tag, 1, 20, MovieSearchResult.class);
	}
	 

}
