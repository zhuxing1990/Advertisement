package com.vunke.chinaunicom.advertisement.manager;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.modle.UpdateDataBean;
import com.vunke.chinaunicom.advertisement.utils.SharedPreferencesUtil;

import java.io.File;
import java.text.NumberFormat;

/**
 * Created by zhuxi on 2017/12/26.
 */
public class DownloadManager {
    private static final String TAG = "DownloadManager";

    private final String KEY_FOR_IMAGE_DOWNLOAD_DATE = "key_for_image_download_date";
    private final String KEY_FOR_VIDEO_DOWNLOAD_TIME = "key_for_video_download_time";

    private String LOCAL_HTTP_IMAGE_PATH;
    private String LOCAL_HTTP_VIDEO_PATH;
    private String LOCAL_SAVE_IMAGE_PATH;
    private String LOCAL_SAVE_VIDEO_PATH;



    public final static String ADVERT_HTTP_SAVE_IMAGE_PATH ="/advert1/image";
    public final static String ADVERT_HTTP_SAVE_VIDEO_PAHT = "/advert1/video";
    public final static String ADVERT_READ_IMAGE_PATH ="/advert2/image";
    public final static String ADVERT_READ_VIDEO_PATH ="/advert2/video";
    public final static String ADVERT_BASE_VIDEO_PATH ="/base/video";
    public final static String ADVERT_BASE_IMAGE_PATH ="/base/image";

    private Context context;
    private  NumberFormat numberFormat;
    public DownloadManager(Context mcontext){
        context = mcontext;
        numberFormat = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        numberFormat.setMinimumFractionDigits(2);

        LOCAL_HTTP_IMAGE_PATH = context.getFilesDir().getAbsolutePath()+ ADVERT_HTTP_SAVE_IMAGE_PATH;
        LOCAL_HTTP_VIDEO_PATH = context.getFilesDir().getAbsolutePath()+ ADVERT_HTTP_SAVE_VIDEO_PAHT;
        LOCAL_SAVE_IMAGE_PATH = context.getFilesDir().getAbsolutePath()+ ADVERT_READ_IMAGE_PATH;
        LOCAL_SAVE_VIDEO_PATH = context.getFilesDir().getAbsolutePath()+ ADVERT_READ_VIDEO_PATH;

    }

    public void GetUpdateData(Context context, UpdateDataBean updateDataBean){
        try {
            if (updateDataBean!=null){
                startDownload(context,updateDataBean);
            }else{
                LogUtil.i(TAG, "GetUpdateData: get data is null,update over");
            }
        }catch (Exception e){
            LogUtil.i(TAG, "GetUpdateData: get data failed ");
            e.printStackTrace();
        }
    }

//    private boolean copyImage = false;
//    private boolean copyVideo = false;
    private   DownloadTask downloadTask;
    private void startDownload(final Context context,final UpdateDataBean updateDataBean) {
        LogUtil.i(TAG, "updateTime: "+updateDataBean.getJson().getUpdateTime());
        String videoDownloadTime = SharedPreferencesUtil.getStringValue(context, KEY_FOR_VIDEO_DOWNLOAD_TIME, "");
        LogUtil.i(TAG, "videoDownloadTime: "+videoDownloadTime);
        if (!TextUtils.isEmpty(updateDataBean.getJson().getUpdateTime())&&!videoDownloadTime.equals(updateDataBean.getJson().getUpdateTime())){
            LogUtil.i(TAG, "video updateTime not equals videoDownloadTime,start updateing");
            try {
                FileManager.localFileDelete(new File(ADVERT_HTTP_SAVE_VIDEO_PAHT));
                DownloadVideo(context, updateDataBean);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            LogUtil.i(TAG, "video updateTime equals videoDownloadTime,not update");
        }
        String imageDownloadDate = SharedPreferencesUtil.getStringValue(context,KEY_FOR_IMAGE_DOWNLOAD_DATE,"");
        LogUtil.i(TAG, "imageDownloadDate: "+imageDownloadDate);
        if (!TextUtils.isEmpty(updateDataBean.getJson().getUpdateTime())&&!videoDownloadTime.equals(updateDataBean.getJson().getUpdateTime())){
            LogUtil.i(TAG, "image updateTime not equals videoDownloadTime,start updateing");
            try {
                FileManager.localFileDelete(new File(ADVERT_HTTP_SAVE_IMAGE_PATH));
                DownloadImage(context, updateDataBean);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            LogUtil.i(TAG, "image updateTime equals videoDownloadTime,not update");
        }
    }

    private void DownloadVideo(final Context context,final UpdateDataBean updateDataBean) {
        if (!TextUtils.isEmpty(updateDataBean.getJson().getVideoDownloadUrl())){
            PostRequest<File> fileRequest = OkGo.<File>post(updateDataBean.getJson().getVideoDownloadUrl());
            downloadTask   = OkDownload.request("VideoDownload",fileRequest).folder(LOCAL_HTTP_VIDEO_PATH).save().register(new DownloadListener("VideoDownload") {
                @Override
                public void onStart(Progress progress) {
                    LogUtil.i(TAG, "onStart: download start ");
                    String totalSize = Formatter.formatFileSize(context, progress.totalSize);
                    String currentSize = Formatter.formatFileSize(context, progress.currentSize);
                    LogUtil.i(TAG, "onStart: 已经下载:"+currentSize);
                    LogUtil.i(TAG, "onStart: 当前文件大小:"+totalSize);
                }

                @Override
                public void onProgress(Progress progress) {
                    String currentSize = Formatter.formatFileSize(context, progress.currentSize);
                    String totalSize = Formatter.formatFileSize(context, progress.totalSize);
                    String speed = String.format("%s/s", Formatter.formatFileSize(context, progress.speed));
                    LogUtil.i(TAG, "onProgress: 已经下载:"+currentSize);
                    LogUtil.i(TAG, "onProgress: 当前文件大小:"+totalSize);
                    LogUtil.i(TAG, "onProgress: 下载速度:"+speed);
                    String fraction = numberFormat.format(progress.fraction);
                    LogUtil.i(TAG, "onProgress: fraction:"+fraction);
                }

                @Override
                public void onError(Progress progress) {
                    LogUtil.i(TAG, "onError: 已经下载:"+ Formatter.formatFileSize(context, progress.currentSize));
                    LogUtil.i(TAG, "onError: date:"+progress.date);
//                    LogUtil.i(TAG, "onError: 下载速度"+ String.format("%s/s", Formatter.formatFileSize(context, progress.speed)));
                    LogUtil.i(TAG, "onError: 当前文件大小:"+ Formatter.formatFileSize(context, progress.totalSize));
                    LogUtil.i(TAG, "onError: status:"+progress.status);
//                    LogUtil.i(TAG, "onError: "+progress.priority);
                    if (downloadTask != null) {
                        downloadTask.remove();
                        downloadTask = null;
                    }
                }

                @Override
                public void onFinish(File file, Progress progress) {
                    if (!file.exists()){
                        LogUtil.i(TAG, "onFinish: file not exists");
                    }else{
                        try {
                            FileManager.copyDirectiory(LOCAL_HTTP_VIDEO_PATH, LOCAL_SAVE_VIDEO_PATH);
                            LogUtil.i(TAG, "copyFile OK fileName:"+file.getName());
                            SharedPreferencesUtil.setStringValue(context, KEY_FOR_VIDEO_DOWNLOAD_TIME, updateDataBean.getJson().getUpdateTime());
                            file.deleteOnExit();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onRemove(Progress progress) {
                    LogUtil.i(TAG, "onRemove: ");
                }
            });
            downloadTask.start();
        }
    }
    private int isSuccess=0;
    private void DownloadImage(final Context context,final UpdateDataBean updateDataBean) {

        if (updateDataBean.getJson().getImageDownloadUrl() != null && !updateDataBean.getJson().getImageDownloadUrl().isEmpty()){

            for (int i = 0; i < updateDataBean.getJson().getImageDownloadUrl().size(); i++) {
                final String url = updateDataBean.getJson().getImageDownloadUrl().get(i).toString();
                isSuccess = updateDataBean.getJson().getImageDownloadUrl().size();
                OkGo.<File>post(url).tag("IMAGE_DOWNLOAD").execute(new FileCallback(LOCAL_HTTP_IMAGE_PATH,null) {
                    @Override
                    public void onSuccess(Response<File> response) {
                        LogUtil.i(TAG, "onSuccess: fileName:"+response.body().getName());
                        try {
                            FileManager.copyDirectiory(LOCAL_HTTP_IMAGE_PATH, LOCAL_SAVE_IMAGE_PATH);
                            LogUtil.i(TAG, "copyFile OK fileName:"+response.body().getName());
                            isSuccess -- ;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        LogUtil.i(TAG, "onError: request url:"+url);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (isSuccess==0){
                            SharedPreferencesUtil.setStringValue(context,KEY_FOR_IMAGE_DOWNLOAD_DATE,updateDataBean.getJson().getUpdateTime());
                        }
                    }
                });
            }
        }
    }



}
