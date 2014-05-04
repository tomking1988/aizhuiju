package com.douban.models;


import com.douban.common.RequestHandler;

/**
 * Created by xtang on 14-3-31.
 */
public abstract class API <B> {

    public boolean secured = false;
    public String api_prefix = "https://api.douban.com/v2/";
    public String shuo_prefix = "https://api.douban.com/shuo/v2/";
    public String bub_prefix = "http://api.douban.com/labs/bubbler/";
    
    protected String idUrl = getURLPrefix();

    public abstract String getURLPrefix();
    public B byId(Long id, Class<B> type) {
    	System.out.println(idUrl+id);
    	return RequestHandler.get(idUrl + id, false, type);
    }
}
