package com.vunke.chinaunicom.advertisement.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.vunke.chinaunicom.advertisement.R;
import com.vunke.chinaunicom.advertisement.base.BaseActivity;
import com.vunke.chinaunicom.advertisement.callback.AdvertCallBack;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.AdvertManage;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
import com.vunke.chinaunicom.advertisement.manager.DownloadManager;
import com.vunke.chinaunicom.advertisement.manager.FileManager;
import com.vunke.chinaunicom.advertisement.manager.URL_Manager;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.modle.NotifyBean;
import com.vunke.chinaunicom.advertisement.modle.UpdateDataBean;
import com.vunke.chinaunicom.advertisement.utils.GroupStategyUtils;
import com.vunke.chinaunicom.advertisement.utils.SharedPreferencesUtil;
import com.vunke.chinaunicom.advertisement.utils.Utils;

import org.json.JSONObject;

import java.util.List;

public class PictureActivity extends BaseActivity {
    private static final String TAG = "PictureActivity";
    private ImageView picture_imageview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        picture_imageview = (ImageView) findViewById(R.id.picture_imageview);
        imagePlayTime = SharedPreferencesUtil.getIntValue(mcontext, "imagePlayTime", this.imagePlayTime);
        initDeviceInfo();
        initPush();
    }
    private DeviceInfoBean deviceInfoBean;
    private void initDeviceInfo() {
        deviceInfoBean = new DeviceInfoBean();
        DevicesManager.queryDevicesInfo(mcontext,deviceInfoBean);
        LogUtil.i(TAG, "initDeviceInfo: deviiceInfoBean:"+deviceInfoBean.toString());
        JSONObject json = AdvertManage.setRequestParams(mcontext, deviceInfoBean);
        AdvertManage.GetAdvertData(mcontext, json, new AdvertCallBack() {
            @Override
            public void onSuccess(UpdateDataBean updateDataBean) {
                Log.i(TAG, "getPicture onSuccess:  ");
            }

            @Override
            public void onError() {
                Log.i(TAG, "getPicture onError: ");
            }
        });
    }
    private NotifyBean notifyBean;
    public void initPush(){
        try {
            JSONObject json = new JSONObject();
            json.put("userName",deviceInfoBean.getUsername());
            json.put("version_code",Utils.getVersionCode(mcontext));
            json.put("version_name",Utils.getVersionName(mcontext));
            LogUtil.i(TAG, "initPush:request json="+json.toString());
            PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL + URL_Manager.ADVERTISEMENT_PUSH_URL).tag(this);
            postRequest.params("json",json.toString());
            postRequest.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        String s = response.body();
                        if (TextUtils.isEmpty(s)){
                            LogUtil.i(TAG, "onSuccess: get push data is null");
                            hsPush = false;
                        }else{
                            LogUtil.i(TAG, "initPush onSuccess: s:"+s);
                            notifyBean = new Gson().fromJson(s,NotifyBean.class);
                            LogUtil.i(TAG, "onSuccess: notifyBean:"+notifyBean.toString());
                            if (notifyBean!=null&&!TextUtils.isEmpty( notifyBean.getCode())){
                                if ( notifyBean.getCode().equals("200")){
                                    LogUtil.i(TAG, "onSuccess: get code =200");
                                    hsPush = true;
                                }else{
                                    hsPush = false;
                                    LogUtil.i(TAG, "onSuccess: get code:"+notifyBean.getCode());
                                }
                            }else{
                                hsPush = false;
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        hsPush = false;
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "initPush onError: ");
                    hsPush = false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            hsPush = false;
        }
    }


    private AnimationDrawable animationDrawable = null;
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "PictureActivity onResume");
        initAnimationDrawable();
        startAnimationDrawable();
    }
    private int imagePlayTime = 5;
    private void initAnimationDrawable() {
        animationDrawable = new AnimationDrawable();
        List<Drawable> picsPath = FileManager.getpicsPath(DownloadManager.ADVERT_READ_IMAGE_PATH, getResources());
        if (picsPath != null && picsPath.size() > 0) {
            for (int i = 0; i < picsPath.size(); i++) {
                animationDrawable.addFrame(picsPath.get(i), imagePlayTime*1000);
            }
        } else {
            picsPath = FileManager.getPicFromAsset(this, getResources());
            for (int i = 0; i < picsPath.size(); i++) {
                animationDrawable.addFrame(picsPath.get(i), imagePlayTime*1000);
            }
        }
        // 设置是否重复播放，false为重复
        animationDrawable.setOneShot(true);
        picture_imageview.setImageDrawable(animationDrawable);
        animationDrawable.start();

    }
    private static Handler handler;
    private static Runnable runnable;
    private void startAnimationDrawable() {
        int duration = 0;
        for (int i = 0; i < animationDrawable.getNumberOfFrames(); i++) {
            duration += animationDrawable.getDuration(i);
        }
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                    startEPG();
            }
        };
        handler.postDelayed(runnable, duration);

    }
    private boolean hsPush = false;
    private void startEPG() {
        LogUtil.i(TAG, "startEPG: hasPush:"+hsPush);
        if (hsPush){
            Intent intent = new Intent(mcontext,NotifyActivity.class);
            intent.putExtra("path",notifyBean.getJson().getPush_url());
            startActivity(intent);
            finish();
        }else{
            LogUtil.i(TAG, "startEPG: not Push");
            GroupStategyUtils.StartEPG(mcontext,deviceInfoBean);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.i(TAG, "PictureActivity onPause: ");
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }
}
