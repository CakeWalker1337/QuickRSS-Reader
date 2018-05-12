package com.tenxgames.maxim.rss_parser.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.tenxgames.maxim.rss_parser.R;
import com.tenxgames.maxim.rss_parser.data.App;
import com.tenxgames.maxim.rss_parser.database.DatabaseHelper;
import com.tenxgames.maxim.rss_parser.exceptions.NoConnectionException;
import com.tenxgames.maxim.rss_parser.listeners.OnChooseChannelListener;
import com.tenxgames.maxim.rss_parser.model.AdapterMode;
import com.tenxgames.maxim.rss_parser.model.Article;
import com.tenxgames.maxim.rss_parser.model.Channel;
import com.tenxgames.maxim.rss_parser.view.ChannelRecyclerAdapter;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* Класс активити, осуществляющего добавление канала в список каналов
* 2 вида добавления:
*  - Добавление с помощью кастомной ссылки
*  - Добавление с помощью предложенных каналов
* */
public class AddChannelActivity extends AppCompatActivity implements OnChooseChannelListener {

    ArrayList<Channel> recommendedChannels;
    String[] channelsListSamples = {"http://www.lenta.ru/rss",
            "http://www.nasa.gov/rss/image_of_the_day.rss",
            "https://www.ed.gov/feed",
            "http://www.npr.org/rss/rss.php",
            "http://www.ksl.com/xml/148.rss",
            "https://news.yandex.ru/index.rss",
            "http://www.starhit.ru/rss/",
            "http://www.anekdot.ws/feed"};
    ArrayList<String> recommendedChannelsLinks;
    RecyclerView recyclerView;

    /*
    * Метод создания активити
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_creator);

        //Добавление названия канала и кнопки "назад" в ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.addChannelString);
        actionBar.setDisplayHomeAsUpEnabled(true);


        //Массив для ссылок, которые будут проходить проверку на валидность
        recommendedChannelsLinks = new ArrayList<>();

        //Массив существующих каналов из БД
        ArrayList<Channel> channels = DatabaseHelper.selectAllChannels();

        //Исключение имеющихся каналов из списка рекоментуемых ссылок
        boolean isExists = false;
        for (String channelsListSample : channelsListSamples) {
            for (Channel channel : channels) {
                if (Objects.equals(channel.getLink(), channelsListSample)) {
                    isExists = true;
                    break;
                }
            }
            if (!isExists)
                recommendedChannelsLinks.add(channelsListSample);
            isExists = false;
        }

        //Массив для рекомендуемых каналов
        recommendedChannels = new ArrayList<>();
        recyclerView = findViewById(R.id.contentRecycler);

        //Создаем адаптер, задаем ему листенер обновления главного вью и устанавливаем в
        //имеющийся ресайклервью
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ChannelRecyclerAdapter cra = new ChannelRecyclerAdapter(recommendedChannels, AdapterMode.RECOMMENDED_CHANNELS_LIST);
        cra.setChooseChannelListener(this);
        recyclerView.setAdapter(cra);

        //Запуск проверки рекомендуемых каналов
        checkRecommendedChannels();
    }

    //Установка кастомного ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_channel_menu, menu);
        return true;
    }

    //Кнопка назад
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Отклик кнопки "готово"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.acceptButton) {
            //Проверка корректности ввода ссылки
            EditText inputBox = findViewById(R.id.inputChannelBox);
            String rssLink = inputBox.getText().toString();
            if (rssLink == "") {
                Toast.makeText(getApplicationContext(), R.string.setChannelLinkWarning,
                        Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
            //Запуск метода подготовки к отправке данных в основное активити
            //Параметр значит, что была введена кастомная ссылка, а не выбрана из списка
            //рекомендуемых каналов. Рекомендуемые каналы имеют позиции от 0 до n
            accept(-1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Подтверждает выбранный канал
    * @param position - позиция в массиве каналов или -1 в случае,
    * если пользователь ввел кастомную ссылку на канал
    * */
    public void accept(int position) {
        // Создание интента и помещение туда полученных данных
        Intent intent = new Intent();
        if (position == -1) {
            //В случае кастомной ссылки
            EditText inputBox = findViewById(R.id.inputChannelBox);
            String rssLink = inputBox.getText().toString();
            intent.putExtra("name", "Unknown");
            intent.putExtra("desc", "Unknown");
            intent.putExtra("link", rssLink);
        } else {
            //В случае выбора канала из списка рекомендуемых каналов
            intent.putExtra("name", recommendedChannels.get(position).getTitle());
            intent.putExtra("desc", recommendedChannels.get(position).getDescription());
            intent.putExtra("link", recommendedChannels.get(position).getLink());
        }

        //Отправка результата и закрытие окна
        setResult(1, intent);
        finish();
    }

    /*
    * Метод, отвечающий за проверку рекомендуемых каналов на валидность
    * */
    private void checkRecommendedChannels() {

        for (int i = 0; i < recommendedChannelsLinks.size(); i++) {
            final int index = i;
            //Для каждой рекомендуемой ссылки на канал отправляем запрос на сервер
            //Если запрос успешен, то канал валидный, иначе инвалид
            App.getApi().getData(recommendedChannelsLinks.get(i)).enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    //Успешный запрос
                    Article art = response.body();
                    if (art != null) {
                        //получаем канал и помещаем его в список
                        Channel channel = new Channel();
                        channel.setTitle(art.getChannel().getTitle());
                        channel.setLink(recommendedChannelsLinks.get(index));
                        channel.setDescription(art.getChannel().getDescription());
                        channel.setValidity(true);
                        recommendedChannels.add(channel);
                        //Обновляем участок списка, куда вставили канал
                        recyclerView.getAdapter().notifyItemInserted(recommendedChannels.size() - 1);
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    //Неуспешный запрос
                    if (t instanceof NoConnectionException)
                        Toast.makeText(getApplicationContext(), R.string.connectionLostMessage,
                                Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    //Метод, слушающий эвент клика по каналу, чтобы отправить выбранный канал в основное активити
    @Override
    public void onChooseChannel(int position) {
        accept(position);
    }
}
