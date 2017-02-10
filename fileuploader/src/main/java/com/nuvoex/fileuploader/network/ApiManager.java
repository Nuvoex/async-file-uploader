package com.nuvoex.fileuploader.network;

import com.nuvoex.fileuploader.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

/**
 * Created by dilip on 04/01/17.
 */

public class ApiManager {

    private static Retrofit retrofit = null;
    private static ApiService service = null;

    private synchronized static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(Constants.Configs.TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(Constants.Configs.TIMEOUT, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.Urls.BASE_URL)
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
