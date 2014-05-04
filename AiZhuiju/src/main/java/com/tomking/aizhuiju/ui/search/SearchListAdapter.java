package com.tomking.aizhuiju.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.douban.models.MovieSearchResult;
import com.douban.models.SimpleMovieSubject;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.common.ImageLoader;
import com.tomking.aizhuiju.dao.AizhuijuDBManager;
import com.tomking.aizhuiju.service.DatabaseService;

/**
 * Created by xtang on 14-4-6.
 */
public class SearchListAdapter extends BaseAdapter {

    private MovieSearchResult searchResult;
    private Context context;
    private LayoutInflater mInflater;
    private SimpleMovieSubject[] subjects;
    private AizhuijuDBManager dbManager;
    private ListView bindedView;
    private DatabaseService mService;

    public SearchListAdapter (Context context, MovieSearchResult searchResult, ListView bindedView) {
        this.searchResult = searchResult;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.subjects = searchResult.getSubjects();
        this.dbManager = AizhuijuDBManager.getInstance(context);
        this.bindedView = bindedView;
    }

    public void setSearchResult(MovieSearchResult searchResult) {
        this.searchResult = searchResult;
        subjects = searchResult.getSubjects();
    }

    @Override
    public int getCount() {
        if(subjects == null) {
            return 0;
        } else {
            return subjects.length;
        }

    }

    @Override
    public Object getItem(int i) {
        return subjects[i];
    }

    @Override
    public long getItemId(int i) {
        try{
            return Long.parseLong(subjects[i].getId());
        }catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_search, null);
            holder.imageView = (ImageView)convertView.findViewById(R.id.show_image);
            holder.titleView = (TextView)convertView.findViewById(R.id.show_title);
            holder.originalTitleView = (TextView)convertView.findViewById(R.id.show_original_title);
            holder.ratingView = (TextView) convertView.findViewById(R.id.show_rating);
            holder.addButton = (ImageButton) convertView.findViewById(R.id.add_show);
            holder.addButton.setTag(subjects[i]);

            holder.titleView.setText(subjects[i].getTitle());
            holder.originalTitleView.setText(subjects[i].getOriginalTitle());
            holder.ratingView.setText("评分:" + subjects[i].getRating().getAverage());
            holder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleMovieSubject subject = (SimpleMovieSubject) view.getTag();
                    if(subject.getSubtype().equals(SimpleMovieSubject.TYPE_MOVIE)) {
                        Toast.makeText(context, R.string.show_wrong_type, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mService.addCurrentShowByID(subject.getId(), subject.getImages().getMedium());

                }
            });
            holder.imageView.setTag(R.id.IMAGE_ID, subjects[i].getImages().getMedium());


            convertView.setTag(holder);
        } else {

            holder = (ViewHolder)convertView.getTag();

            holder.addButton.setTag(subjects[i]);
            holder.titleView.setText(subjects[i].getTitle());
            holder.originalTitleView.setText(subjects[i].getOriginalTitle());
            holder.ratingView.setText("评分:" + subjects[i].getRating().getAverage());
            holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.movie_default_small));
            holder.imageView.setTag(R.id.IMAGE_ID, subjects[i].getImages().getMedium());
        }


        if( i <= (bindedView.getLastVisiblePosition()+1) && subjects[i].getImages() != null && subjects[i].getImages().getMedium() != null ) {
            //MyLog.d("load image");
            ImageLoader imageLoader = new ImageLoader(holder.imageView, context, ImageLoader.ALWAYS_LOAD_FROM_NETWORK);
            imageLoader.execute(subjects[i].getImages().getMedium());

        }



        return convertView;


    }

    public void setDBService(DatabaseService service) {
        this.mService = service;
    }

    private static class ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView originalTitleView;
        public TextView ratingView;
        public ImageButton addButton;

    }
}
