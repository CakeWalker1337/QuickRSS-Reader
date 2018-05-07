package com.example.maxim.rss_parser.model;

import android.content.Context;

import com.example.maxim.rss_parser.exceptions.NoConnectionException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Maxim on 04.05.2018.
 */

public class ConnectionInterceptor implements Interceptor {

    private Context mContext;

    public ConnectionInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkChecker.isOnline(mContext)) {
            throw new NoConnectionException();
        }

        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

}