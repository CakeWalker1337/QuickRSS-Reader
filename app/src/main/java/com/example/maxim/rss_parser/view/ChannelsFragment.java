package com.example.maxim.rss_parser.view;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Card;
import com.example.maxim.rss_parser.view.RecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by Maxim on 04.03.2018.
 */

public class ChannelsFragment extends Fragment {

    public ChannelsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_channels, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerChannels);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("hello4", "descr4"));
        cards.add(new Card("hello5", "descr5"));
        cards.add(new Card("hello6", "descr6"));
        recyclerView.setAdapter(new RecyclerAdapter(cards));

        return v;
    }
}