package com.tenxgames.maxim.rss_parser.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tenxgames.maxim.rss_parser.R;
import com.tenxgames.maxim.rss_parser.data.App;
import com.tenxgames.maxim.rss_parser.database.DatabaseHelper;
import com.tenxgames.maxim.rss_parser.exceptions.NoConnectionException;
import com.tenxgames.maxim.rss_parser.model.AdapterMode;
import com.tenxgames.maxim.rss_parser.model.Article;
import com.tenxgames.maxim.rss_parser.model.Channel;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Класс-фрагмент для отображения списка каналов
 */
public class ChannelsFragment extends Fragment {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout;//вью для обновления ресайклера
    int loadedChannelsCount; //Счетчик проверенных каналов
    private ArrayList<Channel> channels;// список каналов

    public ChannelsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_view, container, false);

        //Идентифицируем необходимые вью

        channels = new ArrayList<>();

        //Идентифицируем вью для обновления свайпом и задаем клик листенер
        swipeLayout = v.findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshChannelsInfo();
            }
        });

        //Идентифицируем ресайклер и устанавливаем ему менеджер и адаптер
        recyclerView = v.findViewById(R.id.contentRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Фиксируем в адаптере мод для отображения зарегистрированных каналов
        recyclerView.setAdapter(new ChannelRecyclerAdapter(channels, AdapterMode.CURRENT_CHANNELS_LIST));

        //Обновляем список каналов
        refreshChannelsInfo();

        return v;
    }


    /**
     * Метод обновления списка каналов
     */
    public void refreshChannelsInfo() {
        //Очищаем ресайклер и получаем каналы из БД
        swipeLayout.setRefreshing(true);
        channels.clear();
        loadedChannelsCount = 0;
        channels.addAll(DatabaseHelper.selectAllChannels());
        //Если каналов в БД нет, то уведомляем пользователя и прерываем обновление
        if (channels.size() == 0) {
            Toast.makeText(getContext(), R.string.noChannelsError,
                    Toast.LENGTH_LONG).show();
            swipeLayout.setRefreshing(false);
            return;
        }
        //Проверяем каналы на валидность
        for (int i = 0; i < channels.size(); i++) {

            final int index = i;
            App.getApi().getData(channels.get(index).getLink()).enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    Article art = response.body();
                    if (art != null) {
                        //Если канал валиден, обновляем канал, полученный из БД
                        Channel channel = channels.get(index);
                        channel.setTitle(art.getChannel().getTitle());
                        channel.setDescription(art.getChannel().getDescription());
                        channel.setValidity(true);
                    }
                    loadedChannelsCount++;
                    //Если канал последний, отображаем изменения в ресайклере
                    if (loadedChannelsCount == channels.size()) {
                        swipeLayout.setRefreshing(false);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    t.printStackTrace();
                    //В случае ошибки делаем канал инвалидным (он будет подсвечиваться красным)
                    channels.get(index).setValidity(false);
                    loadedChannelsCount++;
                    if (loadedChannelsCount == channels.size()) {
                        //Если канал был последним, фиксируем изменения в ресайклере
                        if (t instanceof NoConnectionException)
                            Toast.makeText(getContext(), R.string.connectionLostMessage,
                                    Toast.LENGTH_LONG).show();
                        swipeLayout.setRefreshing(false);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }

    }
}
