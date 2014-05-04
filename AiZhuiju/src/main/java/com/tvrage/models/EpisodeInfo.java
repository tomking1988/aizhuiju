package com.tvrage.models;

import com.tomking.aizhuiju.common.DateHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EpisodeInfo implements Comparable<EpisodeInfo>{
	private String episodeName;
	private String season;
	private String episodeNum;
	private String pubdate;
    private String showName;
    private String showID;
    private int episodeType;
	
	private static final String INFO_SEPARATOR = "\\^";
	private static final String ID_SEPARATOR = "x";
    public static final int LATEST_EPISODE_TYPE = 1;
    public static final int NEXT_EPISODE_TYPE = 2;

    public String getShowName() {
        return this.showName;
    }
	
	public String getPubdate() {
		return this.pubdate;
	}
	
	public String getEpisodeNum() {
		return this.episodeNum;
	}
	
	public String getSeason() {
		return this.season;
	}
	
	public String getEpisodeName() {
		return this.episodeName;
	}

    public String getShowID() {
        return this.showID;
    }

    public int getEpisodeType() {
        return this.episodeType;
    }

    public void setEpisodeNum(String episodeNum) {
        this.episodeNum = episodeNum;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public void setEpisodeType(int type) {
        this.episodeType = type;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public void setShowID(String showID) {
        this.showID = showID;
    }

    public String getEpisodeID() {
        return this.showName + this.season + this.episodeNum;
    }

	public static EpisodeInfo parseEpisodeInfo(String info) {
		
		EpisodeInfo result = new EpisodeInfo();
		
		try{
			 
			String[] infos = info.split(INFO_SEPARATOR);
			 
			String[] episodeID = infos[0].split(ID_SEPARATOR);
			result.season = episodeID[0];
			result.episodeNum = episodeID[1];
			result.episodeName = infos[1];
			result.pubdate = infos[2];
			
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    public boolean isThisWeek() {

        try{
            return DateHelper.isWithinThisWeek(DateHelper.parseEpisodeDate(pubdate));
        }catch(Exception e) {
            return false;
        }
    }

    @Override
    public int compareTo(EpisodeInfo thatEpisode) {
        Date thisDate = DateHelper.parseEpisodeDate(this.pubdate);
        Date thatDate = DateHelper.parseEpisodeDate(thatEpisode.getPubdate());
        return thisDate.compareTo(thatDate);

    }
}


