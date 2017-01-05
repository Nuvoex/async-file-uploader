package com.nuvoex.fileuploader.network;

import android.util.Log;

import com.nuvoex.fileuploader.utils.Consts;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by dilip on 04/01/17.
 */

public class ApiManager {

    private static Retrofit retrofit = null;
    private static ApiService service = null;

    private synchronized static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d(Consts.TAG, message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(Consts.Configs.TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(Consts.Configs.TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Consts.Urls.BASE_URL)
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        if (service == null) {
            service = getClient().create(ApiService.class);
        }
        return service;
    }
}
