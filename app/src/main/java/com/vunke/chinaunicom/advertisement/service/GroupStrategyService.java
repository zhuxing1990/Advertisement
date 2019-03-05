package com.vunke.chinaunicom.advertisement.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
import com.vunke.chinaunicom.advertisement.manager.URL_Manager;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.modle.GroupInfoBean;
import com.vunke.chinaunicom.advertisement.utils.GroupStategyUtils;
import com.vunke.chinaunicom.advertisement.utils.Utils;

import org.json.JSONObject;

public class GroupStrategyService extends Service {
    private static final String TAG = "GroupStrategyService";
    
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate: ");
    }
    private CountDownTimer countDownTimer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand: ");
        initDeviceInfo();
        return super.onStartCommand(intent, flags, startId);
    }
    private DeviceInfoBean deviceInfoBean;
    private void initDeviceInfo() {
        deviceInfoBean = new DeviceInfoBean();
        DevicesManager.queryDevicesInfo(getApplicationContext(),deviceInfoBean);
    cancelCountDownTimer();
    countDownTimer = new CountDownTimer(2000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            LogUtil.i(TAG, "initDeviceInfo: deviceInfoBean:"+deviceInfoBean.toString());
            if (TextUtils.isEmpty(deviceInfoBean.getEPGDomain())){
                LogUtil.i(TAG, "initDeviceInfo: get EPGDomain is null,get EPG failed");
                stopSelf();
                return;
            }
            if (TextUtils.isEmpty(deviceInfoBean.getGroup_id())){
                LogUtil.i(TAG, "initDeviceInfo: get Group_id is null,get EPG failed");
                stopSelf();
                return;
            }
            if (TextUtils.isEmpty(deviceInfoBean.getArea_id())){
                LogUtil.i(TAG, "initDeviceInfo: get Area_id is null,get EPG failed");
                stopSelf();
                return;
            }
                initGroupStrategy();
            //        getGroupStrategy();
        }
    }.start();
}

    private void cancelCountDownTimer() {
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
    }
    private void initGroupStrategy() {
        LogUtil.i(TAG, "initGroupStrategy: ");
        try {
            JSONObject json = new JSONObject();
            json.put("userName",deviceInfoBean.getUsername());
            json.put("version_code" , Utils.getVersionCode(getApplicationContext()));
            json.put("version_name" ,  Utils.getVersionName(getApplicationContext()));
            json.put("EPGDomain" ,  deviceInfoBean.getEPGDomain());
            json.put("EPGGroupNMB" ,  deviceInfoBean.getEPGGroupNMB());
            json.put("Area_id" ,  deviceInfoBean.getArea_id());
            json.put("Group_id" ,  deviceInfoBean.getGroup_id());
            LogUtil.i(TAG, "initGroupStrategy: json="+json.toString());
            PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL + URL_Manager.ADVERTISEMENT_GROUPSTRATEGY).tag(this);
            postRequest.params("json",json.toString());
            postRequest.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        String s = response.body();
                        if (!TextUtils.isEmpty(s)){
                            LogUtil.i(TAG, "initGroupStrategy onSuccess: s:"+s);
                            GroupInfoBean groupInfoBean = new Gson().fromJson(s,GroupInfoBean.class);
                            if (groupInfoBean!=null&&!TextUtils.isEmpty(groupInfoBean.getCode())){
                                 if (groupInfoBean.getCode().equals("200")){
                                     LogUtil.i(TAG, "initGroupStrategy onSuccess: get code =200");
                                     setGroupInfoData(groupInfoBean);
                                 }else{
                                     LogUtil.i(TAG, "initGroupStrategy onSuccess: getcode:"+groupInfoBean.getCode());
                                 }
                            }else{
                                LogUtil.i(TAG, "initGroupStrategy onSuccess: get groupInfo is null or getcode is null");
                            }
                        }else{
                            LogUtil.i(TAG, "initGroupStrategy onSuccess: get groupInfo is null ");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        LogUtil.i(TAG, "initGroupStrategy onSuccess: request failed or get gson from json failed");
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "onError: ");
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    stopSelf();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setGroupInfoData(GroupInfoBean groupInfoBean) {
        groupInfoBean.getJson().setUserName(deviceInfoBean.getUsername());
        GroupStategyUtils.InsertGroupStrategy(getApplicationContext(),groupInfoBean.getJson());
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
