package com.learner.task02.di.module;

import androidx.lifecycle.ViewModelProvider;

import com.learner.task02.utils.ViewModelProviderFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ViewModelProviderFactoryModule {
    @Binds
    public abstract ViewModelProvider.Factory bindViewModelProviderFactory(ViewModelProviderFactory factory);
}
