package com.learner.task02.di.builder;

import com.learner.task02.ui.users.UserActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilderModule {
    @ContributesAndroidInjector
    abstract UserActivity contributeMainActivity();
}
