package com.example.maxim.rss_parser.activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    private Fragment bandFragment;
    private Fragment channelsFragment;
    private FloatingActionButton fab;
    private final int ADD_CHANNEL_ACTIVITY_REQUEST_CODE = 9999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper.openDB(getApplicationContext());
        DatabaseHelper.createTables();
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();
        bandFragment = fm.findFragmentById(R.id.bandFragment);
        channelsFragment = fm.findFragmentById(R.id.channelsFragment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);

        final String[] toolbarSpinnerItemNames = getResources().getStringArray(R.array.toolbarSpinnerItemNames);


        Spinner spinner = toolbar.findViewById(R.id.toolbarSpinner);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_view, toolbarSpinnerItemNames);
        ((ArrayAdapter<String>) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddChannelActivity.class);
                MainActivity.this.startActivityForResult(intent, ADD_CHANNEL_ACTIVITY_REQUEST_CODE);
            }
        });

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


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null)
            return;
        if(requestCode != ADD_CHANNEL_ACTIVITY_REQUEST_CODE)
            return;

        final Channel channel = new Channel();
        channel.setTitle(data.getStringExtra("name"));
        channel.setDescription(data.getStringExtra("desc"));
        channel.setLink(data.getStringExtra("link"));

        App.getApi().getData(channel.getLink()).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                Article art = response.body();
                if (art != null) {
                    channel.setTitle(art.getChannel().getTitle());
                    channel.setDescription(art.getChannel().getDescription());
                    channel.setValidity(true);
                    DatabaseHelper.insertChannel(channel);
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                t.printStackTrace();
                if (t instanceof UnknownHostException)
                    Toast.makeText(getApplicationContext(), R.string.unknownHostMessage +
                                    call.request().url().toString(),
                            Toast.LENGTH_LONG).show();
                if (t instanceof NoConnectionException)
                    Toast.makeText(getApplicationContext(), R.string.connectionLostMessage,
                            Toast.LENGTH_LONG).show();

                channel.setValidity(false);
                DatabaseHelper.insertChannel(channel);

            }
        });

        super.onActivityResult(requestCode, resultCode, data);
    }

}
