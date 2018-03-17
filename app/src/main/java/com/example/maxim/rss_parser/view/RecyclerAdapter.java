package com.example.maxim.rss_parser.view;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.maxim.rss_parser.model.Card;
import com.example.maxim.rss_parser.R;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Card> cards;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        Log.w("Create: ", " s");
        return vh;
    }

    public RecyclerAdapter(ArrayList<Card> newCards)
    {
        cards = newCards;
        for (int i = 0; i<cards.size(); i++)
            Log.w("Count: ", ""+cards.get(i));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder vHolder = (ViewHolder) holder;
        vHolder.helloBox.setText(cards.get(position).getHelloMessage());
        vHolder.descriptionBox.setText(cards.get(position).getDescription());

        Log.w("BindView", "Hello: " + cards.get(position).getHelloMessage() + ", Desc: " + cards.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView helloBox;
        public TextView descriptionBox;
        public ViewHolder(View itemView) {
            super(itemView);
            helloBox = itemView.findViewById(R.id.helloBox);
            descriptionBox = itemView.findViewById(R.id.contentBox);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
