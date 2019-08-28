package com.learner.task02.di.module;

import com.learner.task02.data.network.Api;
import com.learner.task02.data.network.NetworkConnectionInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {
    //Provide OkHttpClient
    @Singleton
    @Provides
    static OkHttpClient provideHttpClient(NetworkConnectionInterceptor interceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(interceptor)
                .build();
    }

    //Provide Retrofit
    @Singleton
    @Provides
    static Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("http://dropbox.sandbox2000.com/intrvw/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
    }

    //Provide Api
    @Provides
    static Api provideApi(Retrofit retrofit) {
        return retrofit.create(Api.class);
    }

}
