package com.learner.task02.data.network;

import com.learner.task02.data.network.response.UserResponse;

import io.reactivex.Flowable;
import retrofit2.http.GET;

public interface Api {
    @GET("users.json")
    Flowable<UserResponse> getUsers();
}
