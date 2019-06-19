package com.example.restapi1;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MyService {

    @GET("jokes/random")
    Call<Joke> getJoke();

}
