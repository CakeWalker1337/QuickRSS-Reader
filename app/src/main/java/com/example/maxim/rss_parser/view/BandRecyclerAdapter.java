package com.example.maxim.rss_parser.view;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.maxim.rss_parser.R;
import com.example.maxim.rss_parser.model.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Класс-адаптер для новостной ленты
 */
public class BandRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final long FADE_DURATION = 300; //Скорость анимации появления итемов в ресайклере
    private ArrayList<Item> items; //массив итемов для отображения
    private int width; //ширина экрана для отображения изображений

    /**
     * Конструктор адаптера
     *
     * @param items - массив данных для отображения
     * @param width - ширина экрана
     */
    public BandRecyclerAdapter(ArrayList<Item> items, int width) {
        this.items = items;
        this.width = width;
    }

    /**
     * Метод создания "карточки" с контентом, который будет отображаться в ресайклере
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //"Надуваем" макет карточки и создаем обертку для данных (ViewHolder)
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Метод, привязывающий данные к карточке новости
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (items == null) return;
        //Если список не пуст, привязываем данные из новости к полям карточки
        ViewHolder vHolder = (ViewHolder) holder;
        Item item = items.get(position);
        vHolder.channelNameBox.setText(item.getChannel().getTitle());
        vHolder.titleBox.setText(item.getTitle());
        vHolder.contentBox.setText(item.getDescription());
        //Задаем отображение ссылок
        vHolder.linkBox.setText(Html.fromHtml("Источник: <a href = \"" + item.getLink() + "\"> " + item.getLink() + "  </a>"));
        vHolder.dateBox.setText(item.getPubDate());

        //Если картинка есть, отображаем ее
        if (item.getEnclosure() != null) {
            Picasso.get().load(item.getEnclosure().getUrl()).resize(width, 0).into(vHolder.imageBox);
        }
        //Создаем анимацию появления
        setFadeAnimation(holder.itemView);
    }

    /**
     * Метод получения размера списка для отображения (используется ресайклером неявно)
     */
    @Override
    public int getItemCount() {
        if (items == null) return 0;
        return items.size();
    }

    /**
     * Метод привязки к ресайклеру (стандартный)
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Метод, применяющий анимацию появления карточки с новостью
     *
     * @param vh - вью карточки с новостью
     */
    private void setFadeAnimation(View vh) {
        //Задаем анимацию изменения прозрачности объекта, скорость анимации и запускаем
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(FADE_DURATION);
        vh.startAnimation(alphaAnimation);
    }

    /**
     * Класс представления данных в виде карточки
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        //объявление полей для отображения данных о новости (обязательно публичные)
        public TextView channelNameBox;
        public TextView titleBox;
        public TextView contentBox;
        public TextView dateBox;
        public TextView linkBox;
        public ImageView imageBox;


        public ViewHolder(View itemView) {
            super(itemView);
            channelNameBox = itemView.findViewById(R.id.channelNameBox);
            titleBox = itemView.findViewById(R.id.titleBox);
            contentBox = itemView.findViewById(R.id.contentBox);
            dateBox = itemView.findViewById(R.id.dateBox);
            linkBox = itemView.findViewById(R.id.linkBox);
            //Устанавливаем переходы по ссылкам внутри текствью
            linkBox.setMovementMethod(LinkMovementMethod.getInstance());
            imageBox = itemView.findViewById(R.id.imageBox);
        }
    }


}
