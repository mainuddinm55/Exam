package com.learner.task02.di.module;

import androidx.lifecycle.ViewModel;

import com.learner.task02.di.ViewModelKey;
import com.learner.task02.ui.users.UserViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel.class)
    public abstract ViewModel bindUserViewModel(UserViewModel userViewModel);
}
