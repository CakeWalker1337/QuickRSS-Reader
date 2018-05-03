package com.example.maxim.rss_parser.view;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;


import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Channel;

import java.io.InputStream;
import java.util.ArrayList;

public class ChannelRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final long FADE_DURATION = 300;
    private ArrayList<Channel> channels;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public ChannelRecyclerAdapter(ArrayList<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (channels == null) return;
        final ViewHolder vHolder = (ViewHolder) holder;
        Channel channel = channels.get(position);
        vHolder.channelNameBox.setText(channel.getTitle());
        vHolder.titleBox.setText(channel.getTitle());
        vHolder.linkBox.setText(Html.fromHtml("Ссылка: <a href = \"" + channel.getLink() + "\"> " + channel.getLink() + "  </a>"));

        setFadeAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        if (channels == null) return 0;
        return channels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView channelNameBox;
        public TextView titleBox;
        public TextView linkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            channelNameBox = itemView.findViewById(R.id.channelNameBox);
            titleBox = itemView.findViewById(R.id.titleBox);
            linkBox = itemView.findViewById(R.id.linkBox);
            linkBox.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void setFadeAnimation(View vh) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(FADE_DURATION);
        vh.startAnimation(alphaAnimation);
    }


}
