package com.douban.models;

public class SimpleMovieSubject extends Bean{
	private String id;
	private String title;
	private String original_title;
	private String alt;
	private DoubanRating rating;
	private String year;
	private String subtype;
	private ImageCollection images;
    public static final String TYPE_MOVIE = "movie";
    public static final String TYPE_TV = "tv";
	
	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}
	
	public String getOriginalTitle() {
		return original_title;
	}
	
	public String getAlt() {
		return alt;
	}
	
	public DoubanRating getRating() {
		return rating;
	}
	
	public String getYear() {
		return year;
	}
	
	public ImageCollection getImages() {
		return images;
	}
	
	public String getSubtype() {
		return subtype;
	}

    public Movie toMovie() {
        Movie show = new Movie(id);
        show.setTitle(title);
        show.setAlt_title(original_title);
        show.setImage(images.getSmall());
        show.setAverageRating(rating.getAverage());
        show.setPubdate(year);
        return show;
    }
	
}
