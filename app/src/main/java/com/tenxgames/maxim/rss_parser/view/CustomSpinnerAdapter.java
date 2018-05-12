package com.tenxgames.maxim.rss_parser.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tenxgames.maxim.rss_parser.R;

/**
 * Created by Maxim on 11.05.2018.
 */

public class CustomSpinnerAdapter extends BaseAdapter {

    private String[] items = {"Лента", "Каналы"};
    private LayoutInflater inflater;
    public CustomSpinnerAdapter(Context context){
        this.inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.simple_spinner_item_view, null);
        TextView tw = view.findViewById(R.id.itemView);
        tw.setText(items[i]);
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.spinner_simple_dropdown_item, parent,
                false);
        TextView tw = view.findViewById(R.id.itemView);
        tw.setText(items[position]);
        return view;
    }

}
