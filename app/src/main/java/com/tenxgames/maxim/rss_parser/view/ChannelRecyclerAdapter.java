package com.tenxgames.maxim.rss_parser.view;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tenxgames.maxim.rss_parser.R;
import com.tenxgames.maxim.rss_parser.database.DatabaseHelper;
import com.tenxgames.maxim.rss_parser.listeners.OnChooseChannelListener;
import com.tenxgames.maxim.rss_parser.model.AdapterMode;
import com.tenxgames.maxim.rss_parser.model.Channel;

import java.util.ArrayList;

/**
 * Класс-адаптер для списка существующих и рекомендуемых каналов
 */
public class ChannelRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final long FADE_DURATION = 300; //Скорость анимации появления каналов в ресайклере
    private OnChooseChannelListener chooseChannelListener; //Листенер для отправки данных
    // во второе активити
    private ArrayList<Channel> channels; //Список каналов
    private AdapterMode currentAdapterMode; //Режим использования адаптера (для рекомендуемых
    // каналов или для существующих каналов)

    /**
     * Конструктор класса, получающий список каналов для отображения и режим отображения
     *
     * @param channels    - список каналов для отображения
     * @param adapterMode - режим отображения (рекомендуемые или существующие каналы)
     */
    public ChannelRecyclerAdapter(ArrayList<Channel> channels, AdapterMode adapterMode) {
        currentAdapterMode = adapterMode;
        this.channels = channels;
    }

    /**
     * Метод присваивающий листенер для хранения в классе. Листенер оповещает класс
     * активити о выборе рекомендуемого канала
     *
     * @param listener - листенер для установки
     */
    public void setChooseChannelListener(OnChooseChannelListener listener) {
        chooseChannelListener = listener;
    }

    /**
     * Метод создания "карточки" с контентом, который будет отображаться в ресайклере
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_card, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Метод, привязывающий данные к карточке канала
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (channels == null) return;

        ViewHolder vHolder = (ViewHolder) holder;
        Channel channel = channels.get(position);
        vHolder.channelNameBox.setText(channel.getTitle());
        vHolder.descBox.setText(channel.getDescription());
        vHolder.linkBox.setText(Html.fromHtml("Ссылка: <a href = \"" + channel.getLink() + "\"> " + channel.getLink() + "  </a>"));
        //Если канал невалидный, меняем цвет на красный
        if (!channel.isValid()) {
            int color = Color.parseColor("#F09E9E");
            vHolder.layoutView.setBackgroundColor(color);
        } else {
            vHolder.layoutView.setBackgroundColor(Color.WHITE);
        }

        //Если режим рекомендуемых каналов, то навешиваем эвенты клика на все элементы карточки
        // и убираем кнопку удаления
        if (currentAdapterMode == AdapterMode.RECOMMENDED_CHANNELS_LIST) {

            CustomClickListener listener = new CustomClickListener(position);
            vHolder.deleteButton.setVisibility(View.INVISIBLE);
            vHolder.itemView.setOnClickListener(listener);
            vHolder.channelNameBox.setOnClickListener(listener);
            vHolder.descBox.setOnClickListener(listener);
            vHolder.linkBox.setOnClickListener(listener);
        } else {
            //Если режим существующих каналов, навешиваем обычный листенер клика на кнопку удаления
            vHolder.deleteButton.setOnClickListener(new DeleteButtonClickListener(channel));
        }
        //Воспроизводим анимацию появления вью в ленте
        setFadeAnimation(holder.itemView);
    }

    /**
     * Метод получения размера списка для отображения (используется ресайклером неявно)
     */
    @Override
    public int getItemCount() {
        if (channels == null) return 0;
        return channels.size();
    }

    /**
     * Метод привязки к ресайклеру (стандартный)
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Метод, применяющий анимацию появления карточки с каналом
     *
     * @param vh - вью карточки с каналом
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
        public TextView channelNameBox;
        public TextView descBox;
        public TextView linkBox;
        public ImageButton deleteButton;
        public LinearLayout layoutView;

        public ViewHolder(View itemView) {
            super(itemView);
            channelNameBox = itemView.findViewById(R.id.channelNameBox);
            descBox = itemView.findViewById(R.id.descBox);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            linkBox = itemView.findViewById(R.id.linkBox);
            //Устанавливаем переходы по ссылкам внутри текствью
            linkBox.setMovementMethod(LinkMovementMethod.getInstance());
            layoutView = itemView.findViewById(R.id.layoutView);
        }
    }

    /**
     * Класс-листенер отвечающий за передачу позиции выбранного канала из рекомендуемых в
     * активити
     */
    public class CustomClickListener implements View.OnClickListener {
        int position;

        public CustomClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            chooseChannelListener.onChooseChannel(position);
        }
    }

    /**
     * Класс-листенер отвечающий за удаление карточки из ленты
     */
    public class DeleteButtonClickListener implements View.OnClickListener {
        private Channel channel;

        public DeleteButtonClickListener(Channel channel) {
            this.channel = channel;
        }

        @Override
        public void onClick(View view) {
            //Удаляем канал из БД
            DatabaseHelper.deleteChannel(channel);
            //Получаем позицию канала в массиве и удаляем его
            int pos = channels.indexOf(channel);
            channels.remove(pos);
            //Обновляем ресайклер на удаленной позиции, чтобы было красиво
            notifyItemRemoved(pos);

        }
    }

}
