package com.example.maxim.rss_parser.exceptions;

import java.io.IOException;

/**
 * Created by Maxim on 04.05.2018.
 */

public class NoConnectionException extends IOException {

    @Override
    public String getMessage() {
        return "NoConnectionException: device was not connected to the internet!";
    }

}