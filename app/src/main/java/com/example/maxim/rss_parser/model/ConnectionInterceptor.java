package com.example.maxim.rss_parser.model;

import android.content.Context;

import com.example.maxim.rss_parser.exceptions.NoConnectionException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Класс-перехватчик для внедрения экспешна об отсутствии интернета в ретрофит
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
        // добавляем интерсептор в цепь вызовов билдера ретро
        Request.Builder builder = chain.request().newBuilder();
        return chain.proceed(builder.build());
    }

}