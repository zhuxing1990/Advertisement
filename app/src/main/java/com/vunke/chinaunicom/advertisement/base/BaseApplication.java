package com.vunke.chinaunicom.advertisement.base;

import android.app.Application;
import android.content.Intent;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.log.LogcatHelper;
import com.vunke.chinaunicom.advertisement.manager.DownloadManager;
import com.vunke.chinaunicom.advertisement.manager.FileManager;
import com.vunke.chinaunicom.advertisement.service.GroupStrategyService;
import com.vunke.chinaunicom.advertisement.utils.SharedPreferencesUtil;
import com.vunke.chinaunicom.advertisement.utils.Utils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhuxi on 2017/12/26.
 */
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG,"versionName:"+ Utils.getVersionName(this));
        LogUtil.i(TAG,"versionCode:"+ Utils.getVersionCode(this));
        LogcatHelper.getInstance(this).start();
        HttpHeaders headers = new HttpHeaders();
        headers.put("Connection","close");
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //全局的读取超时时间
//        builder.readTimeout(5000, TimeUnit.MILLISECONDS);
//        //全局的写入超时时间
//        builder.writeTimeout(5000, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(5000, TimeUnit.MILLISECONDS);
        OkGo.getInstance().init(this).addCommonHeaders(headers).setRetryCount(1).setOkHttpClient( builder.build());
        initGroupStrategy();
        int versionUpgrade = SharedPreferencesUtil.getIntValue(this, "VersionUpgrade", -1);
        LogUtil.i(TAG, "onCreate: versionUpgrade :"+versionUpgrade);
        if (versionUpgrade!=Utils.getVersionCode(this)){
            LogUtil.i(TAG, "onCreate: isAppUpgrade ,clear old app data");
            SharedPreferencesUtil.setIntValue(this,"VersionUpgrade",Utils.getVersionCode(this));
            SharedPreferencesUtil.setStringValue(this,DownloadManager.KEY_FOR_IMAGE_DOWNLOAD_DATE,"");
            SharedPreferencesUtil.setStringValue(this,DownloadManager.KEY_FOR_VIDEO_DOWNLOAD_TIME,"");
            try {
                FileManager.localFileDelete(new File( getFilesDir().getAbsolutePath()+ DownloadManager.ADVERT_READ_VIDEO_PATH));
                FileManager.localFileDelete(new File( getFilesDir().getAbsolutePath()+ DownloadManager.ADVERT_HTTP_SAVE_VIDEO_PAHT));
                FileManager.localFileDelete(new File( getFilesDir().getAbsolutePath()+ DownloadManager.ADVERT_HTTP_SAVE_IMAGE_PATH));
                FileManager.localFileDelete(new File( getFilesDir().getAbsolutePath()+ DownloadManager.ADVERT_READ_IMAGE_PATH));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void initGroupStrategy() {
        LogUtil.i(TAG, "initGroupStrategy: ");
        Intent it = new Intent(this,GroupStrategyService.class);
        startService(it);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        // 程序终止的时候执行
        LogUtil.i(TAG, "onTerminate: ");
        LogcatHelper.getInstance(this).stop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        // 低内存的时候执行
        LogUtil.i(TAG, "onLowMemory");
        System.gc();
    }
//    @Override
//    public void onTrimMemory(int level) {
//        // 程序在内存清理的时候执行
//        super.onTrimMemory(level);
//        LogUtil.i(TAG, "onTrimMemory:"+level);
//        LogcatHelper.getInstance(this).stop();
//        System.gc();
//    }
}
