package com.example.maxim.rss_parser.view;

import android.graphics.Point;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.data.App;
import com.example.maxim.rss_parser.database.DatabaseHelper;
import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.model.Item;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class BandFragment extends Fragment {

    ArrayList<Item> items;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout;
    ProgressBar progressBar;

    public BandFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_band, container, false);

        items = new ArrayList<>();

        swipeLayout = v.findViewById(R.id.swipeLayout);

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateArticles();
            }
        });

        progressBar = v.findViewById(R.id.loadingBar);

        recyclerView = v.findViewById(R.id.recyclerBand);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        recyclerView.setAdapter(new RecyclerAdapter(items, size.x));

        progressBar.setVisibility(View.VISIBLE);
        updateArticles();
        progressBar.setVisibility(View.INVISIBLE);
        return v;

    }


    public void updateArticles() {


        App.getApi().getData("http://www.lenta.ru/rss").enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article art = response.body();
                if (art != null) {
                    for (int i = 0; i < art.getChannel().getItemsSize(); i++) {
                        art.getChannel().getItem(i).setChannel(art.getChannel());
                    }

                    boolean check = false;
                    for (int i = 0; i < channels.size(); i++) {
                        if (Objects.equals(channels.get(i).getTitle(), art.getChannel().getTitle())) {
                            channels.get(i).refreshData(art);
                            check = true;
                            int iterator = 0;
                            while (iterator < items.size()) {
                                if (Objects.equals(items.get(iterator).getChannel().getTitle(), art.getChannel().getTitle()))
                                    items.remove(items.get(iterator));
                                else
                                    iterator++;
                            }

                            break;
                        }
                    }
                    if (!check) {
                        channels.add(art.getChannel());
                        DatabaseHelper.saveChannel(art.getChannel());
                    }
                    for (int i = 0; i < art.getChannel().getItemsSize(); i++) {
                        items.add(art.getChannel().getItem(i));
                    }


                    Collections.sort(items, new Comparator<Item>() {

                        SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH);
                        SimpleDateFormat formatter2 = new SimpleDateFormat("E, dd MMM yyyy HH:mm zzz", Locale.ENGLISH);


                        @Override
                        public int compare(Item item, Item t1) {

                            try {
                                if (formatter.parse(item.getPubDate()).compareTo(formatter.parse(item.getPubDate())) >= 0) {
                                    return 1;
                                }
                            } catch (ParseException e) {
                                try {
                                    if (formatter2.parse(item.getPubDate()).compareTo(formatter2.parse(item.getPubDate())) >= 0) {
                                        return 1;
                                    }
                                } catch (ParseException e2) {
                                    e2.printStackTrace();
                                    Log.w("Date", formatter2.format(new Date()));
                                    return -1;
                                }
                            }
                            return 0;
                        }
                    });

                    Log.w("BandFragment", "items size = " + items.size());
                    recyclerView.getAdapter().notifyDataSetChanged();

                }
                swipeLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException)
                    Toast.makeText(getContext(), R.string.connectionLostMessage, Toast.LENGTH_LONG).show();
                items.clear();
                ArrayList<Item> loadedItems = DatabaseHelper.selectItems();
                items.addAll(loadedItems);
                recyclerView.getAdapter().notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

    }

}
