package com.tvrage.models;


import com.douban.models.Bean;
import com.tomking.aizhuiju.test.MyLog;

public class ShowStatus extends Bean {
	private String showName;
	private String premiered;
	private String started;
	private String ended;
	private EpisodeInfo latestEpisode;
	private EpisodeInfo nextEpisode;
	private String status;
	private String airtime;
	
	private static final String SHOW_NAME = "Show Name";
	private static final String PREMIERED = "Premiered";
	private static final String STARTED = "Started";
	private static final String ENDED = "Ended";
	private static final String LATEST_EPISODE = "Latest Episode";
	private static final String NEXT_EPISODE = "Next Episode";
	private static final String STATUS = "Status";
	private static final String AIRTIME = "Airtime";
	
	private static final String LINE_SEPARATOR = "\n";
	private static final String ELEMENT_SEPARATOR = "@";
	
	public static final String DATE_FORMAT = "MMM/dd/yyyy";
	
	public String getAirtime() {
		return this.airtime;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public EpisodeInfo getNextEpisode() {
        if(this.nextEpisode != null) {
            nextEpisode.setShowName(this.showName);
        }
		return this.nextEpisode;
	}
	
	public EpisodeInfo getLatestEpisode() {
        if(this.latestEpisode != null) {
            latestEpisode.setShowName(this.showName);
        }
		return this.latestEpisode;
	}
	
	public String getEnded() {
		return this.ended;
	}
	
	public String getStarted() {
		return this.started;
	}
	
	public String getPremiered() {
		return this.premiered;
	}
	
	public String getShowName() {
		return this.showName;
	}
	
	
	public static ShowStatus parseShowStatus(String originalInfo) {

		String[] lines = originalInfo.split(LINE_SEPARATOR);
		ShowStatus showStatus = new ShowStatus();
		for(String line : lines) {
			showStatus.set(line.split(ELEMENT_SEPARATOR));
		}
		return showStatus;
	}
	
	public void setShowName(String showName) {
		this.showName = showName;
	}
	
	public void setPremiered(String premiered) {
		this.premiered = premiered;
	}
	
	public void setStarted(String started) {
		this.started = started;
	}
	
	public void setEnded(String ended) {
		this.ended = ended;
	}
	
	public void setLatestEpisode(String latestEpisode) {
		this.latestEpisode = EpisodeInfo.parseEpisodeInfo(latestEpisode);
        this.latestEpisode.setEpisodeType(EpisodeInfo.LATEST_EPISODE_TYPE);
	}
	
	public void setNextEpisode(String nextEpisode) {
		this.nextEpisode = EpisodeInfo.parseEpisodeInfo(nextEpisode);
        this.nextEpisode.setEpisodeType(EpisodeInfo.NEXT_EPISODE_TYPE);
	}
	
	public void setStatus (String status) {
		this.status = status;
	}
	
	public void setAirtime (String airtime) {
		this.airtime = airtime;
	} 
	
	public void set(String[] pair){
		if(pair == null || pair.length != 2){
			return;
		}
		
		if(pair[0].equals(SHOW_NAME)){
			setShowName(pair[1]);
		} else if(pair[0].equals(STARTED)){
			setStarted(pair[1]);
		} else if(pair[0].equals(PREMIERED)){
			setPremiered(pair[1]);
		} else if(pair[0].equals(ENDED)){
			setEnded(pair[1]);
		} else if(pair[0].equals(LATEST_EPISODE)){
			setLatestEpisode(pair[1]);
		} else if(pair[0].equals(NEXT_EPISODE)){
			setNextEpisode(pair[1]);
		} else if(pair[0].equals(STATUS)){
			setStatus(pair[1]);
		} else if(pair[0].equals(AIRTIME)){
			setAirtime(pair[1]);
		}
		
		 
	}
}
