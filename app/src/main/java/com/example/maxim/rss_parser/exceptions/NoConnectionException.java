package com.example.maxim.rss_parser.exceptions;

import java.io.IOException;

/**
 * Класс, описывающий эксепшн отсутствия интернета
 */
public class NoConnectionException extends IOException {

    @Override
    public String getMessage() {
        return "NoConnectionException: device was not connected to the internet!";
    }

}