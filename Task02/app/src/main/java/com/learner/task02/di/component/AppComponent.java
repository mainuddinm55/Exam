package com.learner.task02.di.component;

import android.app.Application;

import com.learner.task02.App;
import com.learner.task02.di.builder.ActivityBuilderModule;
import com.learner.task02.di.module.AppModule;
import com.learner.task02.di.module.ViewModelModule;
import com.learner.task02.di.module.ViewModelProviderFactoryModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityBuilderModule.class,
        ViewModelProviderFactoryModule.class,
        ViewModelModule.class
})
public interface AppComponent extends AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }
}
