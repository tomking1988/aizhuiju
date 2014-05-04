package com.tvrage.common;

import com.douban.common.RequestHandler;
import com.tvrage.models.ShowStatus;

import java.net.HttpURLConnection;
import java.net.URL;



public class TvRageAPI {
	
	private static final String EpisodeExactInfoURL = "http://services.tvrage.com/tools/quickinfo.php?show=%s&exact=1";
    private static final String EpisodeExactInfoURL2 = "http://services.tvrage.com/tools/quickinfo.php?show=%s";

	public static ShowStatus getShowStatusByName(String showName) throws Exception {
		String queryURL = String.format(EpisodeExactInfoURL, showName).replace(" ", "%20");
        String queryURL2 = String.format(EpisodeExactInfoURL2, showName).replace(" ", "%20");
		String result = RequestHandler.getResponse(queryURL);

        if(result == null)
            result = RequestHandler.getResponse(queryURL2);

        if(result == null)
            throw new Exception();

		return ShowStatus.parseShowStatus(result);
		 
	}
}
