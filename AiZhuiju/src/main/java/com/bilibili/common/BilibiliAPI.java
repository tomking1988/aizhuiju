package com.bilibili.common;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tomking.aizhuiju.test.MyLog;
import com.tomking.aizhuiju.util.CommonMethods;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.douban.common.RequestHandler;
import com.bilibili.models.Pair;
import com.bilibili.models.SearchResult;
import com.bilibili.models.SearchResultItem;
import com.bilibili.models.Video;

public class BilibiliAPI {

	private static final String BILIBILI_API = "http://api.bilibili.tv/";
	private static final String BILIBILI_VIDEO_URL_FORMAT = "http://bilibili.kankanews.com/video/av%s/";
	private static final String SEARCH = "search?";
	private static final String VIEW = "view?";
	private static BilibiliAPI instance;
	private static final String APP_KEY = "4ebafd7c4951b366";
	private static final String APP_SECRET_KEY = "8cb98205e9b2ad3669aad0fce12a4c13";
	 
	private BilibiliAPI() {}
	
	public static BilibiliAPI getInstance() {
		if(instance == null) {
			synchronized(BilibiliAPI.class){
				if(instance == null)
					instance = new BilibiliAPI();
			}
		}	
		return instance;
	}
	
	public ArrayList<SearchResultItem> findEpisode(SearchResult searchResult, int season, int episode, String showName) {
        ArrayList<SearchResultItem> foundEpisodes = new ArrayList<SearchResultItem>();
        if(searchResult == null) {
            return foundEpisodes;
        }
        HashMap<Integer, SearchResultItem> result = searchResult.getResult();
		Iterator<Entry<Integer, SearchResultItem>> iterator = result.entrySet().iterator();

		
		while(iterator.hasNext()) {
			Entry<Integer, SearchResultItem> resultItem = iterator.next();
			SearchResultItem item = resultItem.getValue();
			String title = item.getTitle();
			if(findMatch(title, season, episode, showName) && item.isVideo()){
                foundEpisodes.add(item);
            }

		}
		
		return foundEpisodes;
	}
	
	public ArrayList<SearchResultItem> findEpisode(String showName, int season, int episode) {

		SearchResult result = search(showName.toLowerCase());

		ArrayList<SearchResultItem> foundEpisodes = findEpisode(result, season, episode, showName);
		if(foundEpisodes.size() == 0) {
			result = search(showName.toLowerCase() + "+" + season + "季");
            if(result != null)
			    foundEpisodes = findEpisode(result, season, episode, showName);
		}

		
		if(foundEpisodes.size() == 0) {
			result = search(showName.toLowerCase() + "+" + CommonMethods.integerToChinese(season) + "季");
            if(result != null)
			    foundEpisodes = findEpisode(result, season, episode, showName);
		}
		
		
		return foundEpisodes;
	}
	
	private boolean findMatch(String title, int season, int episode, String showName) {
		Pattern p1 = Pattern.compile(season +".+" + episode);
		Pattern p2 = Pattern.compile(CommonMethods.integerToChinese(season) + ".+" + episode);
		Pattern p3 = Pattern.compile("幕后");
		Pattern p4 = Pattern.compile("预告");
		Pattern p5 = Pattern.compile("花絮");
        Pattern p6 = Pattern.compile(showName);
        Pattern p7 = Pattern.compile("剧");
		
		Matcher m1 = p1.matcher(title);
		Matcher m2 = p2.matcher(title);
		Matcher m3 = p3.matcher(title);
		Matcher m4 = p4.matcher(title);
		Matcher m5 = p5.matcher(title);
        Matcher m6 = p6.matcher(title);
        Matcher m7 = p7.matcher(title);

        if(!m6.find() && !m7.find()) {
            return false;
        }
		//return m1.find() || m2.find();
		return (m1.find() || m2.find()) && !m3.find() && !m4.find() && !m5.find();
	}
	
	public SearchResult search(String keywords){
		 try{
			 if(keywords.contains(" ")) {
				 keywords = keywords.replace(" ", "+");
			 }
			Pair appkeyPair = getAppkeyPair();
			Pair keywordPair = new Pair("keyword", keywords);
			ArrayList<Pair> pairs = new ArrayList<Pair>();
			pairs.add(appkeyPair);
			pairs.add(keywordPair);
			String queryURL = BILIBILI_API + SEARCH + generateSignedQuery(pairs, APP_SECRET_KEY);

			String response = RequestHandler.getResponse(queryURL);
             MyLog.d("search response " + response);
			if(response != null) {
				return parseJSON(response, SearchResult.class);
			} else{
				throw new Exception();
			}
		 } catch(Exception e) {
			 e.printStackTrace();
			 return null;
		 }
	}
	
	public Video getVideo(String id, int page) {
		try{
			PairBuilder pBuilder = new PairBuilder();
			pBuilder.addPair(getAppkeyPair());
			pBuilder.addPair("id", id);
			pBuilder.addPair("page", page+"");
			
			String queryURL = BILIBILI_API + VIEW + generateSignedQuery(pBuilder.create(), APP_SECRET_KEY);
			String response = RequestHandler.getResponse(queryURL);
			if(response != null) {
				return parseJSON(response, Video.class);
			} else{
				throw new Exception();
			}
		 } catch(Exception e) {
			 e.printStackTrace();
			 return null;
		 }
	}
	
	public static final String getBilibiliURL(String id){
		return String.format(BILIBILI_VIDEO_URL_FORMAT, id);
	}
	
	public String generateSignedQuery(ArrayList<Pair> pairs, String appSecretKey) throws UnsupportedEncodingException {
		Collections.sort(pairs);
		StringBuilder sb = new StringBuilder();
		for(Pair pair : pairs) {
			if(sb.length() != 0) {
				sb.append("&");
			}
			sb.append(pair.getKey());
			sb.append("=");
			sb.append(URLEncoder.encode(pair.getVal(), "UTF-8"));
		}
		String sign = getMD5(sb.toString() + appSecretKey);
		sb.append("&sign=");
		sb.append(sign);
		return sb.toString();
	}
	
	
	public String getMD5(String md5) {
		   try {
		        MessageDigest md = MessageDigest.getInstance("MD5");
		        byte[] array = md.digest(md5.getBytes("UTF-8"));
		        StringBuffer sb = new StringBuffer();
		        for (int i = 0; i < array.length; ++i) {
		          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
		       }
		        return sb.toString();
		    } catch (Exception e) {
		    
		    }
		    return "";
	}
	
	private Pair getAppkeyPair() {
		return new Pair("appkey", APP_KEY);
	}
	
	private <B> B parseJSON(String jsonString, Class<B> type) throws Exception{
		return RequestHandler.g.fromJson(jsonString, type);
    }
	
	private class PairBuilder {
		private ArrayList<Pair> pairs = new ArrayList<Pair>();
		
		public void addPair(String key, String val){
			pairs.add(new Pair(key, val));
		}
		
		public void addPair(Pair pair) {
			this.pairs.add(pair);
		}
		
		public ArrayList<Pair> create() {
			return this.pairs;
		}
	}
	
}
