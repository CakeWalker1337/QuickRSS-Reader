package com.example.maxim.rss_parser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.data.App;
import com.example.maxim.rss_parser.database.DatabaseHelper;
import com.example.maxim.rss_parser.exceptions.NoConnectionException;
import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.view.BandFragment;
import com.example.maxim.rss_parser.view.ChannelsFragment;
import com.example.maxim.rss_parser.view.CustomSpinnerAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* Главное активити программы.
* Здесь располагаются фрагменты, содержащие список новостей и список каналов.
* Переход осуществляется с помощью спиннера.
* */
public class MainActivity extends AppCompatActivity {

    //Реквест код для активити добавления нового канала
    private final int ADD_CHANNEL_ACTIVITY_REQUEST_CODE = 9999;
    //Необходимые вью
    private FragmentManager fm;
    private BandFragment bandFragment;
    private ChannelsFragment channelsFragment;
    private FloatingActionButton fab;

    /*
    * Метод создания активити.
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Открытие БД и создание таблиц
        DatabaseHelper.openDB(getApplicationContext());
        DatabaseHelper.createTables();

        setContentView(R.layout.activity_main);

        //установка фрагментов
        fm = getSupportFragmentManager();
        bandFragment = (BandFragment) fm.findFragmentById(R.id.bandFragment);
        channelsFragment = (ChannelsFragment) fm.findFragmentById(R.id.channelsFragment);

        //Установка кастомного ActionBar для размещения спиннера
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);

        //Получение названий элементов в спиннере
        final String[] toolbarSpinnerItemNames = getResources().getStringArray(R.array.toolbarSpinnerItemNames);

        //установка спиннера, адаптера к нему а также листенера выбора элемента
        Spinner spinner = toolbar.findViewById(R.id.toolbarSpinner);
        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext());
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Лента")) {

                    fm.beginTransaction()
                            .hide(channelsFragment)
                            .show(bandFragment)
                            .commit();
                    fab.setVisibility(View.INVISIBLE);

                } else {
                    fm.beginTransaction()
                            .hide(bandFragment)
                            .show(channelsFragment)
                            .commit();
                    fab.setVisibility(View.VISIBLE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Настройка кнопки добавления нового канала
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddChannelActivity.class);
                MainActivity.this.startActivityForResult(intent, ADD_CHANNEL_ACTIVITY_REQUEST_CODE);
            }
        });

    }


    /*
    *   Метод, ожидающий результата добавления нового канала.
    *   Результатом добавления нового канала служит сам канал в валидном или инвалидном виде.
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Если не получен интент или не соответствует реквест код, дропаем
        if (data == null)
            return;
        if (requestCode != ADD_CHANNEL_ACTIVITY_REQUEST_CODE)
            return;

        //Формируем канал из интента
        final Channel channel = new Channel();
        channel.setTitle(data.getStringExtra("name"));
        channel.setDescription(data.getStringExtra("desc"));
        channel.setLink(data.getStringExtra("link"));

        //Проверяем канал на валидность, чтобы узнать, необходимо ли занести
        //в БД имеющиеся данные (если канал невалидный), либо получить обновленные, если
        // канал валидный или был задан с помощью ссылки.
        App.getApi().getData(channel.getLink()).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article art = response.body();
                if (art != null) {
                    //Канал получен, сохраняем его и обновляем все ресайклеры
                    channel.setTitle(art.getChannel().getTitle());
                    channel.setDescription(art.getChannel().getDescription());
                    channel.setValidity(true);
                    DatabaseHelper.insertChannel(channel);
                    channelsFragment.refreshChannelsInfo();
                    bandFragment.updateArticles();
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                t.printStackTrace();
                //Проверка на валидность не прошла
                if (t instanceof NoConnectionException)
                    Toast.makeText(getApplicationContext(), R.string.connectionLostMessage,
                            Toast.LENGTH_LONG).show();

                //Помечаем канал как невалидный, чтобы отобразился красным в ресайклере
                //но всё равно сохраняем его в БД
                channel.setValidity(false);
                DatabaseHelper.insertChannel(channel);

            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        DatabaseHelper.closeDB();
        super.onDestroy();
    }

}
