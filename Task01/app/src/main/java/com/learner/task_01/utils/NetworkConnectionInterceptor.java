package com.learner.task_01.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkConnectionInterceptor implements Interceptor {
    private Context context;

    public NetworkConnectionInterceptor(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        if (!isInternetAvailable()) {
            throw new NoInternetException("No Internet Connection");
        }
        return chain.proceed(chain.request());
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
