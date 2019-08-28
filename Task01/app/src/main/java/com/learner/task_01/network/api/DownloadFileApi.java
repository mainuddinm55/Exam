package com.learner.task_01.network.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadFileApi {
    @GET("intrvw/SampleVideo_1280x720_30mb.mp4")
    @Streaming
    Call<ResponseBody> downloadFile();
}
