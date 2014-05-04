package com.douban.models;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.douban.common.Auth;
import com.douban.common.RequestHandler;

public class Bean implements Serializable { 
	 
	private String flat(JsonElement json) throws UnsupportedEncodingException {
		Set<Entry<String, JsonElement>> entrySet = json.getAsJsonObject().entrySet();
		ArrayList<String> attrs = new ArrayList<String>();
		for(Entry<String, JsonElement> e : entrySet) {
			if(e.getValue().isJsonPrimitive()) {
				attrs.add(e.getKey() + "=" + URLEncoder.encode((e.getValue()).getAsString(), RequestHandler.ENCODING));
			} else if (e.getValue().isJsonObject()) {
				attrs.add(flat(e.getValue()));
			}
		}
		 
		StringBuilder sb = new StringBuilder();
		
		if(attrs.size()>0) {
			sb.append(attrs.get(0));
		} else {
			return sb.toString();
		}
		
		for(int i=1; i<attrs.size(); i++) {
			sb.append('&');
			sb.append(attrs.get(i));
		}
		
		return sb.toString();
	}
	
	private String toParas() {
		String result;
		try{
			result = "apiKey="+RequestHandler.apiKey + "&" + flat(RequestHandler.g.toJsonTree(this));
		} catch (Exception e) {
			result = "apiKey="+RequestHandler.apiKey;
		}
		
		return result;
	}
	

	public String flatten(String urlPrefix) {
		return urlPrefix + "?" + toParas();
	} 
	
	 
		 
}
