package com.learner.task02.ui.users;

import com.learner.task02.data.model.User;

import java.util.List;

public interface UserListener {
    void onStarted();

    void onFailure(String message);

    void onSuccess(List<User> users);
}
