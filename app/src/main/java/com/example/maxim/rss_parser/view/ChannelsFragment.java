package com.example.maxim.rss_parser.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.data.App;
import com.example.maxim.rss_parser.database.DatabaseHelper;
import com.example.maxim.rss_parser.exceptions.NoConnectionException;
import com.example.maxim.rss_parser.model.AdapterMode;
import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Channel;

import java.net.UnknownHostException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChannelsFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout;
    private ArrayList<Channel> channels;

    public ChannelsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_view, container, false);

        recyclerView = v.findViewById(R.id.contentRecycler);
        channels = new ArrayList<>();

        swipeLayout = v.findViewById(R.id.swipeLayout);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChannelsInfo();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new ChannelRecyclerAdapter(channels, AdapterMode.CURRENT_CHANNELS_LIST));

        refreshChannelsInfo();

        return v;
    }


    public void refreshChannelsInfo() {
        channels.clear();
        channels.addAll(DatabaseHelper.selectAllChannels());
        if (channels.size() == 0) {
            swipeLayout.setRefreshing(false);
            return;
        }
        for (int i = 0; i < channels.size(); i++) {

            final int index = i;
            App.getApi().getData(channels.get(index).getLink()).enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    Article art = response.body();
                    if (art != null) {
                        Channel channel = channels.get(index);
                        channel.setTitle(art.getChannel().getTitle());
                        channel.setDescription(art.getChannel().getDescription());
                        channel.setValidity(true);
                    }
                    if (index == channels.size() - 1) {
                        swipeLayout.setRefreshing(false);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    t.printStackTrace();
                    if (t instanceof UnknownHostException)
                        Toast.makeText(getContext(), R.string.unknownHostMessage + call.request().url().toString(), Toast.LENGTH_LONG).show();
                    if (t instanceof NoConnectionException)
                        Toast.makeText(getContext(), R.string.connectionLostMessage, Toast.LENGTH_LONG).show();

                    //        ArrayList<Item> loadedItems = DatabaseHelper.selectItemsByChannelId();
                    //      items.addAll(loadedItems);
                    Log.w("CHECK", channels.get(index).getLink());
                    channels.get(index).setValidity(false);
                    if (index == channels.size() - 1) {
                        swipeLayout.setRefreshing(false);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }

    }
}
