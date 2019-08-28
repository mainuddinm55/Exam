package com.learner.task02.ui.users;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.learner.task02.R;
import com.learner.task02.data.model.User;
import com.learner.task02.utils.ViewModelProviderFactory;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import es.dmoral.toasty.Toasty;

public class UserActivity extends DaggerAppCompatActivity implements UserListener {
    private static final String TAG = "UserActivity";
    @Inject
    ViewModelProviderFactory factory;
    private UserViewModel userViewModel;
    private UserRecyclerAdapter userRecyclerAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //Init View
        RecyclerView userRecyclerView = findViewById(R.id.user_recycler_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);

        //Setup Toolbar
        setSupportActionBar(toolbar);
        whiteNotificationBar(toolbar);

        //Initialization View Model
        userViewModel = ViewModelProviders.of(this, factory).get(UserViewModel.class);
        userViewModel.setUserListener(this);
        userViewModel.fetchUser();

        //Init RecyclerView
        userRecyclerAdapter = new UserRecyclerAdapter();
        userRecyclerView.setHasFixedSize(true);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        userRecyclerView.setItemAnimator(new DefaultItemAnimator());
        userRecyclerView.setAdapter(userRecyclerAdapter);
    }

    @Override
    public void onStarted() {
        //Show progress bar when start fetching data
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(String message) {
        progressBar.setVisibility(View.GONE);
        Toasty.error(this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onFailure: " + message);
    }

    @Override
    public void onSuccess(List<User> users) {
        progressBar.setVisibility(View.GONE);
        Log.e(TAG, "onSuccess: " + users.size());
        userRecyclerAdapter.setUserList(users);
    }

    @Override
    protected void onStop() {
        super.onStop();
        userViewModel.clearDisposable();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
}
