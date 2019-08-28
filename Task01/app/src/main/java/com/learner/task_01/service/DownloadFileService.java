package com.learner.task_01.service;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.learner.task_01.R;
import com.learner.task_01.activity.MainActivity;
import com.learner.task_01.network.RetrofitClient;
import com.learner.task_01.network.api.DownloadFileApi;
import com.learner.task_01.network.model.Download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class DownloadFileService extends IntentService {
    public static final String APP_STATE = "app_state";
    private static final String TAG = "DownloadFileService";
    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private boolean isRunning = true;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(APP_STATE)) {
                isRunning = intent.getBooleanExtra("is_running", true);
                if (isRunning) {
                    //Show Progress Dialog
                    Log.e(TAG, "onReceive: Running");
                } else {
                    //Show Progress Notification
                    Log.e(TAG, "onReceive: Close");
                }
            }
        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * name Used to name the worker thread, important only for debugging.
     */
    public DownloadFileService() {
        super("Video Download Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Register broadcast receiver for app is running or not
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(APP_STATE);
        manager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unregister broadcast receiver for app is running or not
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //Create Notification Channel
        createNotificationChannel();

        //Initialization Notification
        initNotification();

        //Initialization Api
        DownloadFileApi api = RetrofitClient.getRetrofitClient(this).getDownloadApi();
        try {
            //Download File
            downloadFile(api.downloadFile().execute());
        } catch (IOException e) {
            Log.e(TAG, "onHandleIntent: ", e);
            sendError(e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    //Notification channel for Android Oreo or Above
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    getString(R.string.notification_channel_id),
                    getString(R.string.notificaiton_channel_name),
                    NotificationCompat.PRIORITY_DEFAULT
            );
            channel.setDescription(getString(R.string.notification_description));
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    //Notification
    private void initNotification() {
        builder = new NotificationCompat.Builder(this, getString(R.string.notification_channel_id));
        builder.setSmallIcon(R.drawable.ic_cloud_download_black_24dp);
        builder.setContentTitle("Download Video");
        builder.setContentText("Download Starting....");
        builder.setAutoCancel(true);
    }

    //Download File
    private void downloadFile(Response<ResponseBody> bodyResponse) throws IOException {
        if (bodyResponse.isSuccessful()) {
            int count;
            byte[] data = new byte[1024 * 30];
            long fileSize = bodyResponse.body().contentLength();
            InputStream inputStream = new BufferedInputStream(bodyResponse.body().byteStream());
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sample_video.mp4");
            if (file.exists()) {
                file.delete();
            }
            OutputStream outputStream = new FileOutputStream(file);
            long total = 0;

            while ((count = inputStream.read(data)) != -1) {
                total += count;
                int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
                double current = Math.round(total / (Math.pow(1024, 2)));
                int progress = (int) ((double) (total * 100) / (double) fileSize);
                Log.e(TAG, "Progress: " + progress);

                Download download = new Download();
                download.setTotalFileSize(totalFileSize);

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);

                outputStream.write(data, 0, count);
            }
            onDownloadComplete();
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } else {
            Log.e(TAG, "downloadFile: " + bodyResponse.errorBody().string());
            Toast.makeText(this, bodyResponse.errorBody().string(), Toast.LENGTH_SHORT).show();
            sendError(bodyResponse.errorBody().string());
        }
    }

    //Send Notification Or Broadcast
    private void sendNotification(Download download) {
        Log.e(TAG, "sendNotification: " + isRunning);
        if (isRunning) {
            manager.cancel(0);
            sendIntent(download);
        } else {
            builder.setProgress(100, download.getProgress(), false);
            builder.setContentText("Downloading File " + download.getCurrentFileSize() + "/" + download.getTotalFileSize() + " MB");
            manager.notify(0, builder.build());
            Log.e(TAG, "sendNotification: " + download.getProgress());
        }
    }

    //Send Data to Activity by Broadcast Receiver
    private void sendIntent(Download download) {
        Intent intent = new Intent(MainActivity.PROGRESS_UPDATE);
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Send Error to Activity
    private void sendError(String message) {
        Intent intent = new Intent(MainActivity.PROGRESS_UPDATE);
        intent.putExtra("error", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //On Download Complete send Notification
    private void onDownloadComplete() {
        Download download = new Download();
        download.setProgress(100);
        sendIntent(download);
        builder.setProgress(0, 0, false);
        builder.setContentText("Video Download Complete");
        manager.notify(0, builder.build());
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        manager.cancel(0);
    }
}
