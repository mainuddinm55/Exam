package com.learner.task_01.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.learner.task_01.network.model.Download;
import com.learner.task_01.service.DownloadFileService;
import com.learner.task_01.R;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PROGRESS_UPDATE = "progress_update";
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_FROM_SETTINGS_REQUEST_CODE = 100;
    private ProgressDialog progressDialog;
    private TextView statusTextView;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PROGRESS_UPDATE)) {
                Download download = intent.getParcelableExtra("download");
                if (download != null) {
                    statusTextView.setVisibility(View.GONE);
                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    if (download.getProgress() == 100) {
                        progressDialog.setMessage(getResources().getString(R.string.download_complete));
                        progressDialog.setProgress(download.getProgress());
                        progressDialog.dismiss();
                        statusTextView.setText(getResources().getString(R.string.download_complete));
                    } else {
                        progressDialog.setProgress(download.getProgress());
                        progressDialog.setTitle("Downloading Video..");
                        progressDialog.setMessage(String.format(Locale.ENGLISH, "Downloaded (%d/%d) MB", download.getCurrentFileSize(), download.getTotalFileSize()));

                    }
                } else {
                    progressDialog.dismiss();
                    String error = intent.getStringExtra("error");
                    statusTextView.setText(error);
                    statusTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Progress Dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Download Starting....");
        progressDialog.setTitle("Download Video.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        MaterialButton downloadBtn = findViewById(R.id.download_file_btn);
        statusTextView = findViewById(R.id.status_text_view);

        //Click Event Listener for Download Video
        downloadBtn.setOnClickListener(this);

    }


    @Override
    protected void onPause() {
        super.onPause();
        //Unregister with Broadcast Receiver for Receiving Download Progress
        unregisterReceiver();

        //Send Broadcast to service the app is not running
        Intent intent = new Intent(DownloadFileService.APP_STATE);
        intent.putExtra("is_running", false);
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register with Broadcast Receiver for Receiving Download Progress
        registerReceiver();

        //Send Broadcast to service the app is running
        Intent intent = new Intent(DownloadFileService.APP_STATE);
        intent.putExtra("is_running", true);
        LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        //Check Storage permission with dexter
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        downloadFile();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            //Show Alert Dialog for need permission
                            showNeedPermissionDialog();
                        } else {
                            Toast.makeText(MainActivity.this, getString(R.string.warning_for_need_permission), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_FROM_SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.e(TAG, "onActivityResult: Permission Granted");
            //If Permission is granted from setting download the file
            downloadFile();
        } else {
            Log.e(TAG, "onActivityResult: Permission Denied");
            Toast.makeText(MainActivity.this, getString(R.string.warning_for_need_permission), Toast.LENGTH_SHORT).show();

        }
    }

    //If  User permanent deny storage permission show dialog for grant permission from settings
    private void showNeedPermissionDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(getString(R.string.need_permission));
        builder.setMessage(getString(R.string.need_permission_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.goto_settings), (dialog, which) -> {
            dialog.cancel();
            gotoSettings();
        });

        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            dialog.cancel();
            Toast.makeText(MainActivity.this, getString(R.string.warning_for_need_permission), Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }

    //Navigate to Setting for grant storage permission
    private void gotoSettings() {
        Intent settingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        settingIntent.setData(uri);
        startActivityForResult(settingIntent, PERMISSION_FROM_SETTINGS_REQUEST_CODE);
    }

    //Start Download File Service
    private void downloadFile() {
        Intent intent = new Intent(this, DownloadFileService.class);
        startService(intent);
        progressDialog.show();
    }

    //Register Broadcast Receiver
    private void registerReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        manager.registerReceiver(broadcastReceiver, intentFilter);
    }

    //Unregister Broadcast Receiver
    private void unregisterReceiver() {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(broadcastReceiver);
    }
}
