package com.tomking.aizhuiju.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.douban.models.Movie;
import com.tomking.aizhuiju.R;
import com.tomking.aizhuiju.common.FontManager;
import com.tomking.aizhuiju.common.ImageLoader;
import com.tomking.aizhuiju.dao.AizhuijuDBManager;
import com.tomking.aizhuiju.webservice.CommandBuilder;

import java.util.ArrayList;

/**
 * Created by xtang on 14-4-2.
 */
public class CurrentShowsFragment extends Fragment {

    public static final String ARG_OBJECT = "object";
    private static CurrentShowsListAdapter showsListAdapter;
    private Handler mHandler = new ShowsHandler();
    private AizhuijuDBManager dbManager;
    private ArrayList<Movie> shows;
    private ListView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        rootView = (ListView)inflater.inflate(
                R.layout.list_current_shows, container, false);

        dbManager = AizhuijuDBManager.getInstance(getActivity());
        dbManager.connect();
        shows = dbManager.getCurrentShows();
        //dbManager.close();

        showsListAdapter = new CurrentShowsListAdapter(getActivity(), shows, rootView);
        rootView.setAdapter(showsListAdapter);
        rootView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new DeleteShowDialogFragment(getActivity().getResources().getString(R.string.delete_show_title) +
                    shows.get(i).getAltTitle(), i
                ).show(getFragmentManager(), "delete_show");
                return false;
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbManager.connect();

        ArrayList<Movie> newShows = new ArrayList<Movie>();
        newShows = dbManager.getCurrentShows();
        //dbManager.close();
        shows.clear();
        for(Movie show : newShows) {
            shows.add(show);
        }
        showsListAdapter.notifyDataSetChanged();

    }

    private class DeleteShowDialogFragment extends DialogFragment {

        private String title;
        private int index;

        public DeleteShowDialogFragment(String title, int index) {
             this.title = title;
             this.index = index;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(title)
                    .setPositiveButton(R.string.delete_show_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            dbManager.connect();
                            dbManager.deleteCurrentShow(shows.get(index).getIdLong());
                            dbManager.deleteEpisodeInfoByName(shows.get(index).getTitle());
                            //dbManager.close();
                            shows.remove(index);
                            showsListAdapter.notifyDataSetChanged();
                            rootView.invalidate();
                            DeleteShowDialogFragment.this.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            DeleteShowDialogFragment.this.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static class CurrentShowsListAdapter extends BaseAdapter {

        private ArrayList<Movie> shows;
        private LayoutInflater mInflater = null;
        private ListView rootView;
        private Context context;


        public CurrentShowsListAdapter(Context context, ArrayList<Movie> shows, ListView rootView) {
            super();
            this.shows = shows;
            this.context = context;
            this.mInflater = LayoutInflater.from(context);
            this.rootView = rootView;
        }

        public void updateItem(int position, Movie show) {
            show.setIdLong(shows.get(position).getIdLong());
            shows.set(position, show);
            getView(position, rootView.getChildAt(position), rootView);

        }

        @Override
        public int getCount() {
            return shows.size();
        }

        @Override
        public Object getItem(int i) {
            return shows.get(i);
        }


        @Override
        public long getItemId(int i) {

            return Long.parseLong(shows.get(i).getIdLong());
        }

        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_show, null);
                holder.imageView = (ImageView)convertView.findViewById(R.id.show_image);
                holder.titleView = (TextView)convertView.findViewById(R.id.show_title);
                holder.pubdateView = (TextView)convertView.findViewById(R.id.pubdate);
                holder.ratingView = (TextView) convertView.findViewById(R.id.rating);

                holder.titleView.setText(shows.get(position).getDoubleTitle() );
                //FontManager.setRobotoRegular(context, holder.titleView);
                holder.pubdateView.setText(shows.get(position).getPubdate());
                holder.ratingView.setText("评分: " +shows.get(position).getAverageRating());
                holder.imageView.setTag(R.id.IMAGE_ID, shows.get(position).getImage());
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
                holder.titleView.setText(shows.get(position).getDoubleTitle());
                holder.pubdateView.setText(shows.get(position).getPubdate());
                holder.ratingView.setText("评分: " + shows.get(position).getRating().getAverage());
                holder.imageView.setTag(R.id.IMAGE_ID, shows.get(position).getImage());
            }

            if(shows.get(position).getImage() != null) {
                ImageLoader imageLoader = new ImageLoader(holder.imageView, context);
                imageLoader.execute(shows.get(position).getImage());
            }



            return convertView;


        }

        /*
        public void setShows(ArrayList<Movie> shows) {
            this.shows = shows;
        }*/
    }

    private static class ViewHolder {
        public ImageView imageView;
        public TextView titleView;
        public TextView pubdateView;
        public TextView ratingView;
    }

    private class ShowsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case CommandBuilder.GET_MOVIE_BY_ID :

                    Movie show = (Movie) msg.obj;

                    int position = 0;
                    for(; position<showsListAdapter.getCount(); position++) {

                        if(((Movie)showsListAdapter.getItem(position)).getUrlId().equals(show.getUrlId())) {
                            showsListAdapter.updateItem(position, show);
                            break;
                        }
                    }
                    break;
            }
        }
    }
}
