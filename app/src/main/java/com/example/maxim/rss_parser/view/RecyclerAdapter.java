package com.example.maxim.rss_parser.view;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Card;
import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Item;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item> items;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public RecyclerAdapter(ArrayList<Item> items) {

        this.items = items;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(items == null) return;
        ViewHolder vHolder = (ViewHolder) holder;
        vHolder.channelNameBox.setText(items.get(position).getChannelName());
        vHolder.titleBox.setText(items.get(position).getTitle());
        vHolder.contentBox.setText(items.get(position).getDescription());
        vHolder.dateBox.setText(items.get(position).getPubDate());
    }

    @Override
    public int getItemCount() {
        if(items == null) return 0;
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView channelNameBox;
        public TextView titleBox;
        public TextView contentBox;
        public TextView dateBox;

        public ViewHolder(View itemView) {
            super(itemView);
            channelNameBox = itemView.findViewById(R.id.channelNameBox);
            titleBox = itemView.findViewById(R.id.titleBox);
            contentBox = itemView.findViewById(R.id.contentBox);
            dateBox = itemView.findViewById(R.id.dateBox);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
