package com.douban.models;

import com.douban.common.RequestHandler;

public abstract class BookMovieMusicAPI<B, RT, RV>  extends API <B>{
	private String popTagsUrl = getURLPrefix() + "%s/tags";
	private String reviewsPostUrl = getURLPrefix() + "reviews";
	private String reviewUpdateUrl = getURLPrefix() + "review/%s";
	protected String searchUrl = getURLPrefix() + "/search";
	 
	public RT search(String query, String tag, int page, int count, Class<RT> type) {
		return RequestHandler.get((new Search(query, tag, (page-1)*count, count)).flatten(searchUrl), false, type);
	}
}
