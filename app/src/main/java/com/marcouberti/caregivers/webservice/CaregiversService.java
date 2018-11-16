package com.marcouberti.caregivers.webservice;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CaregiversService {

    @GET("/api")
    Call<String> listCaregivers(@Query("seed") String seed,
                                         @Query("page") Integer page,
                                         @Query("results") Integer results);

}
