package com.douban.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tomking.aizhuiju.test.MyLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;

/**
 * Created by xtang on 14-3-31.
 * Inspired by https://github.com/jinntrance/douban-scala
 */
public class RequestHandler {

    public static String apiKey = Auth.api_key;
    private static final int TIME_OUT = 60 * 1000;
    private static final int persistenceTimeout = 10 * 60;
    private static final String PUT = "PUT";
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String DELETE = "DELETE";
    public static final String ENCODING = "UTF-8";
    private static final String emptyJSON = "{}";
    private static String accessToken = "";
    public static Gson g = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private RequestHandler(){}

    public static <B> B get (String url, boolean secured, Class<B> type) {
        String newUrl = secured? url : addApiKey(url.replace("https://", "http://"));
        try{
            HttpURLConnection c = getData(newUrl, false);
            System.out.println(c.toString());
            return parseJSON(c, type);
        } catch (Exception e) {
            return null;
        }

    }

    //A general method for retrieving content from url
    public static String getResponse(String url) {
        try{
            HttpURLConnection c = getData(url, false);

            InputStream responseStream;

            if (succeed(c.getResponseCode()))
                responseStream = c.getInputStream();
            else
                throw new Exception();
            return getOriginalResponse(responseStream);

        } catch (Exception e) {
            //MyLog.d("Connection exception" + e.toString());
            return null;
        }
    }

    private static String getOriginalResponse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        try{
            String line = reader.readLine();
            while (null != line) {
                content.append(line);
                content.append("\n");
                line = reader.readLine();
            }
            return content.toString();
        } catch (Exception e) {
            return "";
        }

    }

    private static String addApiKey(String url) {
        String key = "apikey=" + apiKey;
        if (-1 == url.indexOf('?'))
            return url + "?" + key;
        else
            return url + "&" + key;

    }

    private static <B> B parseJSON(HttpURLConnection c, Class<B> type) throws Exception{

        InputStream responseStream;
        if (succeed(c.getResponseCode()))
            responseStream = c.getInputStream();
        else
            responseStream = c.getErrorStream();
        String v = getResponse(responseStream);

        c.disconnect();

        if (succeed(c.getResponseCode())) {
            return g.fromJson(v, type);
        }
        else
            throw new Exception();
    }

    private static HttpURLConnection getData(String url, boolean authorized) throws  IOException{
        HttpURLConnection connection = getConnection(url);
        connect(connection, authorized);
        return connection;
    }

    private static HttpURLConnection getConnection(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
        return connection;
    }

    private static void connect(HttpURLConnection c, boolean authorized) throws IOException {
        c.setUseCaches(true);
        c.setConnectTimeout(TIME_OUT);
        c.setReadTimeout(TIME_OUT);
        //Android 2.3及以后的HttpConnectionUrl自动使用gzip，故此处就不再添加
        c.setRequestProperty("Connection", "Keep-Alive");
        c.setRequestProperty("User-agent","Aizhuiju/1.0.0 (aizhuiju.tom@gmail.com)");
        c.setRequestProperty("Keep-Alive", "timeout="+persistenceTimeout);
        //添加认证的access token
        if (authorized)
            c.setRequestProperty("Authorization", "Bearer "+accessToken);
        c.setRequestProperty("Charset", ENCODING);

        /*
        if ((c.getRequestMethod() == POST || c.getRequestMethod() == PUT) && null != request) {
            if (request.files.size == 0) {
                c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                val out = new BufferedOutputStream(c.getOutputStream)
                val paras = request.toParas
                debug("request body-->" + URLDecoder.decode(paras, ENCODING))
                out.write(paras.getBytes(ENCODING))
                out.flush()
                out.close()
            } else {
                val b = genBoundary
                c.setRequestProperty("Content-Type", s"multipart/form-data;boundary=$b")
                debug(s"request body with boundary-->$b")
                val out = new BufferedOutputStream(c.getOutputStream)
                upload(b, out, g.toJsonTree(request))
                request.files.foreach {
                    case (k, v) => uploadFile(b, out, k, v, withoutFile = false)
                }
                val boundaryString = s"\r\n--$b--\r\n"
                out.write(boundaryString.getBytes(ENCODING))
                out.flush()
                out.close()
                print(boundaryString)
            }
        }
        */
        c.connect();
    }

    private static String getResponse(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder content = new StringBuilder();
        try{
            String line = reader.readLine();
            while (null != line) {
                content.append(line);
                line = reader.readLine();
            }
            return content.toString();
        } catch (Exception e) {
            return "";
        }

    }

    private static boolean succeed(int code) {
       return code >= HttpURLConnection.HTTP_OK && code <= HttpURLConnection.HTTP_PARTIAL;
    }
}
