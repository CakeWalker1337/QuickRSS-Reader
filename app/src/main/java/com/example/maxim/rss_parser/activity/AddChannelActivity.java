package com.example.maxim.rss_parser.activity;

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

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.data.App;
import com.example.maxim.rss_parser.database.DatabaseHelper;
import com.example.maxim.rss_parser.exceptions.NoConnectionException;
import com.example.maxim.rss_parser.listeners.OnChooseChannelListener;
import com.example.maxim.rss_parser.model.AdapterMode;
import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.view.ChannelRecyclerAdapter;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChannelActivity extends AppCompatActivity implements OnChooseChannelListener {

    ArrayList<Channel> recommendedChannels;
    String[] channelsListSamples = {"http://www.lenta.ru/rss", "http://www.nasa.gov/rss/image_of_the_day.rss"};
    ArrayList<String> recommendedChannelsLinks;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_creator);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.addChannelString);

        actionBar.setDisplayHomeAsUpEnabled(true);

        recommendedChannelsLinks = new ArrayList<>();
        ArrayList<Channel> channels = DatabaseHelper.selectAllChannels();

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
        recommendedChannels = new ArrayList<>();
        recyclerView = findViewById(R.id.contentRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        ChannelRecyclerAdapter cra = new ChannelRecyclerAdapter(recommendedChannels, AdapterMode.RECOMMENDED_CHANNELS_LIST);
        cra.setChooseChannelListener(this);
        recyclerView.setAdapter(cra);

        checkRecommendedChannels();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_channel_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.acceptButton) {
            EditText inputBox = findViewById(R.id.inputChannelBox);
            String rssLink = inputBox.getText().toString();
            if (rssLink == "") {
                Toast.makeText(getApplicationContext(), R.string.setChannelLinkWarning,
                        Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
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
        Intent intent = new Intent();
        if (position == -1) {
            EditText inputBox = findViewById(R.id.inputChannelBox);
            String rssLink = inputBox.getText().toString();
            intent.putExtra("name", "Unknown");
            intent.putExtra("desc", "Unknown");
            intent.putExtra("link", rssLink);
        } else {
            intent.putExtra("name", recommendedChannels.get(position).getTitle());
            intent.putExtra("desc", recommendedChannels.get(position).getDescription());
            intent.putExtra("link", recommendedChannels.get(position).getLink());
        }

        setResult(1, intent);
        finish();
    }

    private void checkRecommendedChannels() {

        for (int i = 0; i < recommendedChannelsLinks.size(); i++) {
            final int index = i;
            App.getApi().getData(recommendedChannelsLinks.get(i)).enqueue(new Callback<Article>() {
                @Override
                public void onResponse(Call<Article> call, Response<Article> response) {
                    Article art = response.body();
                    if (art != null) {
                        Channel channel = new Channel();
                        channel.setTitle(art.getChannel().getTitle());
                        channel.setLink(recommendedChannelsLinks.get(index));
                        channel.setDescription(art.getChannel().getDescription());
                        channel.setValidity(true);
                        recommendedChannels.add(channel);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<Article> call, Throwable t) {
                    t.printStackTrace();
                    if (t instanceof UnknownHostException)
                        Toast.makeText(getApplicationContext(), "Невозможно подключиться к серверу " +
                                        call.request().url().toString(),
                                Toast.LENGTH_LONG).show();
                    if (t instanceof NoConnectionException)
                        Toast.makeText(getApplicationContext(), R.string.connectionLostMessage,
                                Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onChooseChannel(int position) {
        accept(position);
    }
}
