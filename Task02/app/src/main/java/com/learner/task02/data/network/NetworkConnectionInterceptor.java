package com.learner.task02.data.network;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.learner.task02.utils.NoInternetException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Response;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkConnectionInterceptor implements Interceptor {
    private Application application;

    @Inject
    public NetworkConnectionInterceptor(Application application) {
        this.application = application;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (!isInternetAvailable()) {
            throw new NoInternetException("No Internet Connection");
        }
        return chain.proceed(chain.request());
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
