package com.feedback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by abdul on 29/7/16.
 */
public class ListCustomAdapter extends BaseAdapter implements ListAdapter {

    private final Context context;
    private final ArrayList<ListResult> itemsArrayList;

    public ListCustomAdapter(Context context, ArrayList<ListResult> itemsArrayList) {
        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }
    @Override
    public int getCount() {
        return itemsArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) ((Activity)context).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_items,null);
            holder = new ViewHolder();

            holder.date = (TextView)view.findViewById(R.id.date);
            holder.items = (TextView)view.findViewById(R.id.item);
            holder.rating = (RatingBar)view.findViewById(R.id.rating);
            holder.results = (TextView)view.findViewById(R.id.result);
            view.setTag(holder);
        }else{
            holder=(ViewHolder)view.getTag();
        }

        holder.date.setText(itemsArrayList.get(position).date);
        holder.items.setText(itemsArrayList.get(position).item);
        holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                holder.results.setVisibility(View.VISIBLE);
                int ratings = (int)holder.rating.getRating();
                itemsArrayList.get(position).rating = ratings;
                switch (ratings){
                    case 1:holder.results.setText("Very Bad");
                        break;
                    case 2:holder.results.setText("Bad");
                        break;
                    case 3:holder.results.setText("Normal");
                        break;
                    case 4:holder.results.setText("Good");
                        break;
                    case 5:holder.results.setText("Best");
                        break;
                    default:holder.results.setVisibility(View.GONE);
                }
             //   Toast.makeText(context,ratings+"Star", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public static class ViewHolder{
        public TextView date;
        public TextView items;
        public RatingBar rating;
        public TextView results;
    }
}
