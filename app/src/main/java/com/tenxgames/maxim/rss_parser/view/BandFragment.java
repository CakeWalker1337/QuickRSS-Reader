package com.tenxgames.maxim.rss_parser.view;

import android.graphics.Point;
import android.os.AsyncTask;
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
import com.tenxgames.maxim.rss_parser.model.Article;
import com.tenxgames.maxim.rss_parser.model.Channel;
import com.tenxgames.maxim.rss_parser.model.Item;
import com.tenxgames.maxim.rss_parser.model.NetworkChecker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Класс фрагмента, размещающего новостную ленту
 */
public class BandFragment extends Fragment {

    ArrayList<Item> items; //массив новостей ленты
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeLayout; //вью для обновления ресайклера
    ArrayList<Channel> channels; //массив каналов
    ArrayList<SimpleDateFormat> formats; //массив форматов дат для сортировки ленты
    int loadedChannelsCount; //Счетчик проверенных каналов

    public BandFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Задаем известные форматы дат
        formats = new ArrayList<>();
        formats.add(new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss ZZZ",
                Locale.ENGLISH));
        formats.add(new SimpleDateFormat("E, dd MMM yyyy HH:mm zzz",
                Locale.ENGLISH));
        formats.add(new SimpleDateFormat("dd MMM yyyy HH:mm:ss ZZZ",
                Locale.ENGLISH));
        formats.add(new SimpleDateFormat("E MMM dd HH:mm:ss ZZZ yyyy",
                Locale.ENGLISH));

        View v = inflater.inflate(R.layout.fragment_view, container, false);

        items = new ArrayList<>();

        //Идентифицируем свайп-вью для обновления ресайклера
        swipeLayout = v.findViewById(R.id.swipeLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateArticles();
            }
        });

        //Идентифицируем ресайклер, ставим адаптер и лэйаут менеджер
        recyclerView = v.findViewById(R.id.contentRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        recyclerView.setAdapter(new BandRecyclerAdapter(items, size.x));

        //обновляем новости
        updateArticles();

        return v;
    }

    /**
     * Метод обновления ленты новостей
     */
    public void updateArticles() {
        //Очищаем ленту
        items.clear();
        recyclerView.getAdapter().notifyDataSetChanged();
        loadedChannelsCount = 0;
        //Получаем список каналов
        channels = DatabaseHelper.selectAllChannels();
        if (channels.size() == 0) {
            //Если список пуст, то останавливаем обновление и выводим уведомление
            swipeLayout.setRefreshing(false);
            Toast.makeText(getActivity().getApplicationContext(), R.string.noChannelsError, Toast.LENGTH_LONG).show();
            return;
        }
        //Чекаем подключение к интернету
        if (NetworkChecker.isOnline(getContext())) {
            //Подключение есть - чекаем каналы на валидность и формируем ленту новостей
            for (int i = 0; i < channels.size(); i++) {
                final int index = i;
                App.getApi().getData(channels.get(i).getLink()).enqueue(new Callback<Article>() {
                    @Override
                    public void onResponse(Call<Article> call, Response<Article> response) {
                        Article art = response.body();
                        if (art != null) {
                            //Канал живой, обновляем данные канала и присваиваем
                            // канал каждому итему канала. Это нужно для отображения данных о
                            // канале в адаптере BandRecyclerAdapter (заголовок канала)
                            art.getChannel().setId(channels.get(index).getId());
                            for (int i = 0; i < art.getChannel().getItemsSize(); i++) {
                                art.getChannel().getItem(i).setChannel(art.getChannel());
                            }

                            //Приводим дату новости к нормальному формату и добавляем новость
                            // в масив
                            for (int i = 0; i < art.getChannel().getItemsSize(); i++) {
                                art.getChannel().getItem(i).setPubDate(getDateOfItem(art.getChannel().getItem(i)).toString());
                                items.add(art.getChannel().getItem(i));
                            }
                        }
                        //Если канал был последним, сортируем новости по дате и времени
                        // сохраняем новости в БД в асинхронном запросе и обновляем ресайклер
                        loadedChannelsCount++;
                        if (loadedChannelsCount == channels.size()) {
                            swipeLayout.setRefreshing(false);
                            sortItems();
                            SaveItemsAsyncTask asyncTask = new SaveItemsAsyncTask();
                            asyncTask.execute(items);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                    }

                    @Override
                    public void onFailure(Call<Article> call, Throwable t) {
                        t.printStackTrace();
                        //В случае неудачи отправляем сообщение об ошибке
                        // и если канал был последним, сортируем новости по дате и времени
                        // сохраняем новости в БД в асинхронном запросе и обновляем ресайклер

                        loadedChannelsCount++;
                        if (loadedChannelsCount == channels.size()) {
                            if (t instanceof NoConnectionException)
                                Toast.makeText(getContext(), R.string.connectionLostMessage,
                                        Toast.LENGTH_LONG).show();
                            swipeLayout.setRefreshing(false);
                            sortItems();
                            SaveItemsAsyncTask asyncTask = new SaveItemsAsyncTask();
                            asyncTask.execute(items);
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                    }
                });
            }
        } else {
            //Если нет подключения к интернету, выгружаем из БД последние
            // сохраненные новости и обновляем ресайклер
            ArrayList<Item> items1 = DatabaseHelper.selectAllItems();
            items.addAll(items1);
            swipeLayout.setRefreshing(false);
            recyclerView.getAdapter().notifyDataSetChanged();
            Toast.makeText(getContext(), R.string.connectionLostMessage,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Метод, преобразующий тип даты и времени новости к общему формату
     *
     * @param item - новость для форматирования
     * @return форматированный объект даты новости или null в случае ошибки.
     */
    public Date getDateOfItem(Item item) {
        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(item.getPubDate());
            } catch (ParseException ignored) {
            }
        }
        return null;
    }

    /**
     * Метод, сортирующий новости в порядке новизны с помощью компаратора
     */
    public void sortItems() {
        Collections.sort(items, new Comparator<Item>() {

            @Override
            public int compare(Item item, Item item2) {
                Date itemDate1 = getDateOfItem(item);
                Date itemDate2 = getDateOfItem(item2);

                return (itemDate1.compareTo(itemDate2) > 0) ? -1 : 1;
            }
        });

    }

    /**
     * Асинхронный класс для сохранения новостей в БД.
     */
    private class SaveItemsAsyncTask extends AsyncTask<ArrayList<Item>, Void, Void> {

        /**
         * Метод выполняющий удаление новостей из БД, а затем сохраняющий их заново
         *
         * @param items - массив новостей
         */
        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Item>... items) {
            DatabaseHelper.deleteAllItems();
            DatabaseHelper.insertItems(items[0]);
            return null;
        }
    }

}
