package com.douban.models;

public class Movie extends Bean{

	
	private String id;
	private String title;
	private String alt_title;
	private DoubanRating rating;
	private String image;
    private String idLong;
	 
	private String summary;
	private MovieAttribute attrs;

    public Movie(String id) {
        this.idLong = id;
    }
	
	public MovieAttribute getAttrs() {
		return attrs;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getImage() {
		return image;
	}
	
	public String getId() {
		return id;
	}

    public String getIdLong() {
        if(idLong == null && id != null) {
            String[] temp = id.split("/");
            return temp[temp.length - 1];
        }

        return idLong;
    }

    public void setIdLong(String idLong) {
        this.idLong = idLong;
    }

    public void setPubdate(String pubdate) {
        if(this.attrs == null) {
            this.attrs = new MovieAttribute();
        }

        attrs.setPubdate(pubdate);
    }

    public String getUrlId() {
        if(idLong != null)
            return "http://api.douban.com/movie/"+idLong;
        else
            return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlt_title(String alt_title) {
        this.alt_title = alt_title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setAverageRating(double average) {
        if(this.rating == null) {
            this.rating = new DoubanRating();
        }

        this.rating.setAverage(average);
    }
	
	public String getTitle() {

		return title;
	}
	
	public String getAltTitle() {
        if(alt_title == null || alt_title.equals(""))
            return title;
        else {
            if(alt_title.indexOf("/") >= 0) {
                return alt_title.split("/")[0];
            }
            return alt_title;
        }

	}

    public String getDoubleTitle() {
        String result = getAltTitle();
        if(alt_title != null) {
            result += " | " + getTitle();
        }
        return result;
    }
	
	public DoubanRating getRating() {
		return rating;
	}

    public String getPubdate () {
        if(attrs == null)
            return "";
        else
            return attrs.getPubdate();
    }

    public String getAverageRating() {
        if(rating == null) {
            return "";
        } else {
            return rating.getAverage()+"";
        }
    }
	
}
