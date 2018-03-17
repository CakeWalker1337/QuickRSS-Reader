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
 * A placeholder fragment containing a simple view.
 */
public class BandFragment extends Fragment {

    public BandFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_band, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.recyclerBand);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(new Card("hello1", "descr1"));
        cards.add(new Card("hello2", "descr2"));
        cards.add(new Card("hello3", "descr3"));
        recyclerView.setAdapter(new RecyclerAdapter(cards));

        return v;

    }
}
