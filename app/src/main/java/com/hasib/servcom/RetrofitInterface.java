package com.hasib.servcom;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/*
 * Created by S M Al Hasib on 1/21/21 5:38 PM
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 1/21/21 5:29 PM
 */

public interface RetrofitInterface {

    @POST("/callDetails")
    Call<Void> sendCallDetails(@Body CallDetails callDetails);

    @POST("/message")
    Call<Void> sendMessage(@Body Message message);
}
