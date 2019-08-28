package com.learner.task02.data.repositoy;

import com.learner.task02.data.network.Api;
import com.learner.task02.data.network.response.UserResponse;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class UserRepository {
    private Api api;

    @Inject
    public UserRepository(Api api) {
        this.api = api;
    }

    //Fetching all user form server
    public Flowable<UserResponse> getUsers() {
        return api.getUsers();
    }
}
