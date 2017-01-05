package com.nuvoex.fileuploader.network;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Url;

/**
 * Created by dilip on 04/01/17.
 */

public interface ApiService {

    @PUT
    Call<ResponseBody> uploadFile(@Url String url, @Body RequestBody body);
}
