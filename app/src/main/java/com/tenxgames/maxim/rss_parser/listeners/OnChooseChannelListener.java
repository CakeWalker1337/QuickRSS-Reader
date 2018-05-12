package com.tenxgames.maxim.rss_parser.listeners;

/**
 * Интерфейс, использующийся для передачи выбранной позиции канала
 * из адаптера ресайклера в активити
 */
public interface OnChooseChannelListener {
    void onChooseChannel(int position);
}
