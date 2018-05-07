package com.example.maxim.rss_parser.view;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.database.DatabaseHelper;
import com.example.maxim.rss_parser.listeners.OnChooseChannelListener;
import com.example.maxim.rss_parser.model.AdapterMode;
import com.example.maxim.rss_parser.model.Channel;

import java.util.ArrayList;

public class ChannelRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final long FADE_DURATION = 300;
    OnChooseChannelListener chooseChannelListener;
    private ArrayList<Channel> channels;
    private AdapterMode currentAdapterMode;

    public ChannelRecyclerAdapter(ArrayList<Channel> channels, AdapterMode adapterMode) {
        currentAdapterMode = adapterMode;
        this.channels = channels;
    }

    public void setChooseChannelListener(OnChooseChannelListener listener) {
        chooseChannelListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (channels == null) return;

        ViewHolder vHolder = (ViewHolder) holder;
        Channel channel = channels.get(position);
        vHolder.channelNameBox.setText(channel.getTitle());
        vHolder.descBox.setText(channel.getDescription());
        vHolder.linkBox.setText(Html.fromHtml("Ссылка: <a href = \"" + channel.getLink() + "\"> " + channel.getLink() + "  </a>"));
        Log.w("REFRESH_CHAN", channel.getLink());
        if (!channel.isValid()) {
            int color = Color.parseColor("#F07E7E");
            vHolder.layoutView.setBackgroundColor(color);
        } else {
            vHolder.layoutView.setBackgroundColor(Color.WHITE);
        }


        if (currentAdapterMode == AdapterMode.RECOMMENDED_CHANNELS_LIST) {

            CustomClickListener listener = new CustomClickListener(position);
            vHolder.deleteButton.setVisibility(View.INVISIBLE);
            vHolder.itemView.setOnClickListener(listener);
            vHolder.channelNameBox.setOnClickListener(listener);
            vHolder.descBox.setOnClickListener(listener);
            vHolder.linkBox.setOnClickListener(listener);
        } else {
            final int pos = position;
            vHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseHelper.deleteChannel(channels.get(pos));
                    channels.remove(pos);
                    notifyItemRemoved(pos);
                }
            });

        }
        setFadeAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        if (channels == null) return 0;
        return channels.size();
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView channelNameBox;
        public TextView descBox;
        public TextView linkBox;
        public ImageButton deleteButton;
        public LinearLayout layoutView;

        public ViewHolder(View itemView) {
            super(itemView);
            channelNameBox = itemView.findViewById(R.id.channelNameBox);
            descBox = itemView.findViewById(R.id.descBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            linkBox = itemView.findViewById(R.id.linkBox);
            linkBox.setMovementMethod(LinkMovementMethod.getInstance());
            layoutView = itemView.findViewById(R.id.layoutView);
        }
    }

    public class CustomClickListener implements View.OnClickListener {
        int position;

        public CustomClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            chooseChannelListener.onChooseChannel(position);
        }
    }


}
