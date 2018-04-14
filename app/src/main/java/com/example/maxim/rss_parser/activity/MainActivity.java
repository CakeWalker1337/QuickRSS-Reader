package com.example.maxim.rss_parser.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.data.App;
import com.example.maxim.rss_parser.database.DatabaseHelper;
import com.example.maxim.rss_parser.model.Article;
import com.example.maxim.rss_parser.model.Channel;
import com.example.maxim.rss_parser.model.Item;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fm;
    private Fragment bandFragment;
    private Fragment channelsFragment;
    public int Index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper.openDB(getApplicationContext());

        Object obj = getArrayOrObj();


        fm = getSupportFragmentManager();
        bandFragment = fm.findFragmentById(R.id.bandFragment);
        channelsFragment = fm.findFragmentById(R.id.channelsFragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setDisplayShowTitleEnabled(false);

        final String[] toolbarSpinnerItemNames = getResources().getStringArray(R.array.toolbarSpinnerItemNames);


        Spinner spinner = toolbar.findViewById(R.id.toolbarSpinner);
        SpinnerAdapter spinnerAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item_view, toolbarSpinnerItemNames);
        ((ArrayAdapter<String>) spinnerAdapter).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Лента")) {

                    fm.beginTransaction()
                            .hide(channelsFragment)
                            .show(bandFragment)
                            .commit();
                } else {
                    fm.beginTransaction()
                            .hide(bandFragment)
                            .show(channelsFragment)
                            .commit();

                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplication(), "Rect", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private Object getArrayOrObj()
    {
        ArrayList<Item> ar = new ArrayList<>();
        ar.add(new Item());
        return ar;
    }

}
