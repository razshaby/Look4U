package com.razchen.look4u.server;

import android.util.Log;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;


public class Service {

    private static final String TAG = "Service";


    private static String encodeValue(String value) {
        try {

            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public Call findMatches(Callback callback, String categoryPath, String seekerFavorGender, String seekerUserId, int fromAge, int toAge, String categoryChoicesArrInString) {


        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(1, TimeUnit.SECONDS);

        HttpUrl url = null;

        url = new HttpUrl.Builder()
                .scheme("http")
                .host(Finals.IP)
                .port(Finals.PORT)
                .addPathSegment("findMatches")
                .addQueryParameter("categoryPath", encodeValue(categoryPath))
                .addQueryParameter("seekerFavorGender", seekerFavorGender)
                .addQueryParameter("seekerUserId", seekerUserId)
                .addQueryParameter("fromAge", String.valueOf(fromAge))
                .addQueryParameter("toAge", String.valueOf(toAge))
                .addQueryParameter("categoryChoicesArrInString", encodeValue(categoryChoicesArrInString))
                .build();

        Log.d(TAG, "onResponse findMatches: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);


        return call;

    }


    public Call getSimilarQuestionFromDB(Callback callback, String DBRef, String question, String idsArray, String advertiserUserId) {


        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(1, TimeUnit.SECONDS);

        HttpUrl url = null;

        url = new HttpUrl.Builder()
                .scheme("http")
                .host(Finals.IP)
                .port(Finals.PORT)
                .addPathSegment("getSimilarQuestion")
                .addQueryParameter("DBRef", encodeValue(DBRef))
                .addQueryParameter("question", encodeValue(question))
                .addQueryParameter("idsArray", encodeValue(idsArray))
                .addQueryParameter("advertiserUserId", encodeValue(advertiserUserId))
                .build();

        Log.d(TAG, "onResponse findMatches: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);


        return call;

    }


}

