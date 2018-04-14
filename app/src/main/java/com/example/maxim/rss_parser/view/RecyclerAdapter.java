package com.example.maxim.rss_parser.view;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Item;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final long FADE_DURATION = 300;
    private InputStream is;
    private ArrayList<Item> items;
    private int width;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public RecyclerAdapter(ArrayList<Item> items, int width) {
        this.items = items;
        this.width = width;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (items == null) return;
        final ViewHolder vHolder = (ViewHolder) holder;
        Item item = items.get(position);
        vHolder.channelNameBox.setText(item.getChannel().getTitle());
        vHolder.titleBox.setText(item.getTitle());
        vHolder.contentBox.setText(item.getDescription());
        vHolder.linkBox.setText(Html.fromHtml("Источник: <a href = \"" + item.getLink() + "\"> " + item.getLink() + "  </a>"));
        vHolder.dateBox.setText(item.getPubDate());

        if (item.getEnclosure() == null)
            Log.w("Enclosure", "NULL");
        else {
            Picasso.get().load(item.getEnclosure().getUrl()).resize(width, 0).into(vHolder.imageBox);
        }

        setFadeAnimation(holder.itemView);
    }

    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView channelNameBox;
        public TextView titleBox;
        public TextView contentBox;
        public TextView dateBox;
        public TextView linkBox;
        public ImageView imageBox;

        public ViewHolder(View itemView) {
            super(itemView);
            channelNameBox = itemView.findViewById(R.id.channelNameBox);
            titleBox = itemView.findViewById(R.id.titleBox);
            contentBox = itemView.findViewById(R.id.contentBox);
            dateBox = itemView.findViewById(R.id.dateBox);
            linkBox = itemView.findViewById(R.id.linkBox);
            linkBox.setMovementMethod(LinkMovementMethod.getInstance());
            imageBox = itemView.findViewById(R.id.imageBox);
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
