package com.douban.models;

public class MovieAttribute {
 
	private String[] website;
	private String[] pubdate;
	private String[] language;
	private String[] title;
	private String[] country;
	private String[] writer;
	private String[] director;
	private String[] cast;
	private String[] episodes;
	private String[] year;
	private String[] movie_type;
	 
	
	public String[] getWebsite() {
		return this.website;
	}
	 
	public String getPubdate() {
		return this.pubdate[0];
	}
	
	public String[] getLanguage(){
		return this.language;
	}

	public String[] getTitle() {
		return this.title;
	}
	
	public String[] getCountry() {
		return this.country;
	}
	
	public String[] getWriter() {
		return this.writer;
	}
	
	public String[] getDirector() {
		return this.director;
	}
	
	public String[] getCast() {
		return this.cast;
	}
	
	public String getEpisodes() {
		return this.episodes[0];
	}
	
	public String getYear() {
		return this.year[0];
	}
	
	public String[] getMovieType() {
		return this.movie_type;
	}

    public void setPubdate(String pubdate) {
        this.pubdate = new String[]{pubdate};
    }
	 
	
	
}
