package com.vunke.chinaunicom.advertisement.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Response;
import com.vunke.chinaunicom.advertisement.log.LogUtil;

import java.io.File;

/**
 * Created by zhuxi on 2017/7/27.
 */
public class SilenceInstallUtils {
    private static final String TAG = "SilenceInstallUtils";

    public static void InstallApk(Context context, File ApkPath){
        try {
            LogUtil.i(TAG, "InstallApk: start SilenceInstall Service");
            Intent intent = new Intent("com.android.SilenceInstall.Start");
            intent.setDataAndType(Uri.fromFile(ApkPath),"application/vnd.android.package-archive");
            context.startService(intent);
//            Intent intent = new Intent("com.android.silenceinstaller.InstallService");
//            intent.setPackage("com.android.silenceinstaller");
//            intent.setAction("com.android.SilenceInstall.Start");
//            intent.setDataAndType(Uri.fromFile(ApkPath),"application/vnd.android.package-archive");
//            context.startService(intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void DonloadEpgApk(final Context context, String ApkPath){
        LogUtil.i(TAG, "DonloadEpgApk: ApkPath:"+ApkPath);
        if (TextUtils.isEmpty(ApkPath)){
            LogUtil.i(TAG, "DonloadEpgApk: get ApkPath is null");
            return;
        }
        OkGo.<File>post(ApkPath).tag(context).execute(new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                LogUtil.i(TAG, "onSuccess: download apk success");
                InstallApk(context,response.body());
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                LogUtil.i(TAG, "onError: download failed");
            }
        });

    }
}
