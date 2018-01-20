package com.vunke.chinaunicom.advertisement.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.XExecutor;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.utils.PermissionsUtils;

/**
 * Created by zhuxi on 2017/12/26.
 */
public class BaseActivity extends AppCompatActivity implements XExecutor.OnAllTaskEndListener{
    private static final String TAG = "BaseActivity";
    public BaseActivity mcontext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mcontext = this;
        OkDownload okDownload = OkDownload.getInstance();
        okDownload.getThreadPool().setCorePoolSize(3);
        okDownload.addOnAllTaskEndListener(this);
        initPermission();
    }
    private void initPermission() {
        int permission = ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean hasPermission = permission== PackageManager.PERMISSION_GRANTED;
        if (!hasPermission){
            PermissionsUtils.checkSDCardPermission(mcontext);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionsUtils.REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取权限
            } else {
                showToast("权限被禁止，无法下载文件！");
            }
        }
    }
    /**
     * 吐司
     */
    public void showToast(CharSequence text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch(level){
            case TRIM_MEMORY_UI_HIDDEN:
                LogUtil.i(TAG, "onTrimMemory: level:TRIM_MEMORY_UI_HIDDEN");
                break;
            case TRIM_MEMORY_RUNNING_MODERATE:
                LogUtil.i(TAG, "onTrimMemory: level:TRIM_MEMORY_RUNNING_MODERATE");
                break;
            case TRIM_MEMORY_RUNNING_LOW:
                LogUtil.i(TAG, "onTrimMemory: level:TRIM_MEMORY_RUNNING_LOW");
                break;
            case TRIM_MEMORY_RUNNING_CRITICAL:
                LogUtil.i(TAG, "onTrimMemory: level:TRIM_MEMORY_RUNNING_CRITICAL");
                break;
        }
        System.gc();
    }

    @Override
    public void onAllTaskEnd() {
        LogUtil.i("okdownload", "onAllTaskEnd: 所有下载任务已结束");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
