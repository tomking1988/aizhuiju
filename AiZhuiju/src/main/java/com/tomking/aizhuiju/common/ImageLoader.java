package com.tomking.aizhuiju.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.douban.models.SimpleMovieSubject;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.test.MyLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by xtang on 14-4-5.
 */
public class ImageLoader extends AsyncTask<String, Integer, BitmapDrawable> {

    private final WeakReference<ImageView> imageViewReference;
    private final Context context;
    private boolean alwaysLoadFromNetwork = false;
    private SimpleMovieSubject subject;
    public static final int IS_LOADED = 1;
    private static HashMap<String,WeakReference<BitmapDrawable>> cache = new HashMap<String, WeakReference<BitmapDrawable>>();
    private static LinkedList<String> queue = new LinkedList<String>();
    private static final int CACHE_CAPACITY = 20;
    public static final boolean ALWAYS_LOAD_FROM_NETWORK = true;
    private String imageURL;
    public static final String IMAGE_LOAD_ONCE_TAG = "load once";

    public ImageLoader(ImageView imageView, Context context) {
        this.imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
    }

    public ImageLoader(ImageView imageView, Context context, boolean alwaysLoadFromNetwork) {
        this.imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
        this.alwaysLoadFromNetwork = alwaysLoadFromNetwork;
    }

    @Override
    protected BitmapDrawable doInBackground(String... urls) {

        imageURL = urls[0];
        String imageID = imageURL.substring(imageURL.lastIndexOf("/")+1);

        BitmapDrawable image = null;

        if(getFromCache(imageID) != null) {
            image = getFromCache(imageID);
        }

        /*
        if(image != null) {
            MyLog.d("Load from cache");
        }*/

        if(image == null && !alwaysLoadFromNetwork) {

            image = getImageFromInternal(imageID);
        }

        if(image == null) {
            //MyLog.d("Load from internet");
            image = getImageFromNetwork(imageURL, imageID);

        }

        return image;
    }

    @Override
    protected void onPostExecute(BitmapDrawable value) {
        // if cancel was called on this task or the "exit early" flag is set then we're done
        final ImageView imageView = imageViewReference.get();
        if ( imageView != null && value != null) {
            synchronized (imageView) {
                String image_id = (String)imageView.getTag(R.id.IMAGE_ID);
                if(imageURL.equals(image_id))
                    imageView.setImageDrawable(value);

            }

        }
    }

    private void addToCache(String imageID, BitmapDrawable image){

        if(cache.size() >= CACHE_CAPACITY) {
            String deletedID = queue.poll();
            cache.remove(deletedID);
        }

        if(cache.containsKey(imageID)) {
            queue.remove(imageID);
            cache.remove(imageID);
        }

        cache.put(imageID, new WeakReference<BitmapDrawable>(image));
        queue.offer(imageID);
    }

    private BitmapDrawable getFromCache(String imageID) {
        if(cache.get(imageID) == null) {
            return null;
        } else {
            return cache.get(imageID).get();
        }

    }

    private BitmapDrawable getImageFromInternal(String imageID) {


        try{

            FileInputStream fis = context.openFileInput(imageID);
            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
            BitmapDrawable image = new BitmapDrawable( context.getResources(), imageBitmap);

            if(imageBitmap != null) {
                addToCache(imageID, image);
            }

            return image;

        }catch(Exception e) {
            MyLog.d(e.toString());
        }


        return null;
    }

    private BitmapDrawable getImageFromNetwork(String url, String imageID){

        try{
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();


            if(alwaysLoadFromNetwork) {
                Bitmap imageBitmap = BitmapFactory.decodeStream(is);
                BitmapDrawable image = new BitmapDrawable( context.getResources(), imageBitmap);
                addToCache(imageID, image);
                return image;
            }

            FileOutputStream fos = context.openFileOutput(imageID, Context.MODE_PRIVATE);
            CopyStream(is, fos);
            fos.close();

            return getImageFromInternal(imageID);

        }catch(Exception e) {
            MyLog.d(e.toString());
        }


        return null;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
            MyLog.d(ex.toString());
        }
    }
}
