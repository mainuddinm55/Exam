package com.learner.task02.ui.users;

import androidx.lifecycle.ViewModel;

import com.learner.task02.data.repositoy.UserRepository;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserViewModel extends ViewModel {
    private CompositeDisposable disposable = new CompositeDisposable();
    private UserRepository userRepository;

    private UserListener userListener;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public void setUserListener(UserListener userListener) {
        this.userListener = userListener;
    }

    public void fetchUser() {
        userListener.onStarted();
        disposable.add(
                userRepository.getUsers()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(userResponse -> userListener.onSuccess(userResponse.getUsers()),
                                throwable -> userListener.onFailure(throwable.getMessage()))
        );
    }

    public void clearDisposable() {
        disposable.clear();
    }
}
