package com.vunke.chinaunicom.advertisement.manager;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.vunke.chinaunicom.advertisement.callback.AdvertCallBack;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.modle.UpdateDataBean;
import com.vunke.chinaunicom.advertisement.utils.SharedPreferencesUtil;
import com.vunke.chinaunicom.advertisement.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhuxi on 2018/2/5.
 */
public class AdvertManage {
    private static final String TAG = "AdvertManage";
    public static JSONObject setRequestParams(Context context, DeviceInfoBean deviceInfoBean){
        LogUtil.i(TAG, "setRequestParams: ");
        JSONObject json = new JSONObject();
        try {
            json.put("userName",deviceInfoBean.getUsername())
//                    .put("userName","073108962949A@tv")
                    .put("version_code", Utils.getVersionCode(context))
                    .put("version_name",Utils.getVersionName(context))
                    .put("stbModle", Build.MODEL)
                    .put("EPGDomain" ,  deviceInfoBean.getEPGDomain())
                    .put("EPGGroupNMB",deviceInfoBean.getEPGGroupNMB())
                    .put("Area_id",deviceInfoBean.getArea_id())
                    .put("Group_id",deviceInfoBean.getGroup_id())
                    .put("stb_id",deviceInfoBean.getStb_id());
            LogUtil.i(TAG, "setRequestParams: json:"+json.toString());
            return json;
        }catch (JSONException e){
            LogUtil.i(TAG, "setRequestParams: onError");
            e.printStackTrace();
        }
        return json;
    }

    public static void GetAdvertData(final Context mcontext,JSONObject json, final AdvertCallBack advertCallBack){
        try {
            OkHttpClient ok = new OkHttpClient();
            ok.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
            PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL+URL_Manager.ADVERTISEMENT_IPTVSTRATEGY_URL).tag(TAG).retryCount(1).client(ok);
            postRequest.params("json", json.toString());
            postRequest.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String s = response.body();
                    if (TextUtils.isEmpty(s)) {
                        LogUtil.i(TAG, "initAdvert onSuccess: get data is null");
                        advertCallBack.onError();
                        return;
                    }
                    LogUtil.i(TAG, "initAdvert onSuccess: s:"+s);
                    try {
                        UpdateDataBean  updateDataBean = new Gson().fromJson(s, UpdateDataBean.class);
                        if (updateDataBean==null||TextUtils.isEmpty(updateDataBean.getCode())){
                            LogUtil.i(TAG, "initAdvert: get data is null or get code is null,update over");
                            advertCallBack.onError();
                            return;
                        }
                        if (!updateDataBean.getCode().equals("200")){
                            LogUtil.i(TAG, "onSuccess: get code :"+updateDataBean.getCode());
                            advertCallBack.onError();
                            return;
                        }else{
                            LogUtil.i(TAG, "onSuccess: get code =200");
                            int templateTYPE = updateDataBean.getJson().getTemplateTYPE();
                            LogUtil.i(TAG, "initAdvert onSuccess: templateTYPE:"+templateTYPE);
                            if (templateTYPE!=10){
                                SharedPreferencesUtil.setBooleanValue(mcontext, "isVideoStream", false);
                                DownloadManager downloadManager = new DownloadManager(mcontext);
                                downloadManager.GetUpdateData(mcontext,updateDataBean);
                            }
                            switch (templateTYPE){
                                case 1:
                                    int imagePlayTime = updateDataBean.getJson().getImagePlayTime();
                                    int videoPlayTime = updateDataBean.getJson().getVideoPlayTime();
                                    if (videoPlayTime>0){
                                        SharedPreferencesUtil.setBooleanValue(mcontext, "isPicture", false);
                                        LogUtil.i(TAG, "StartAdvert: 播放视频 videoPlayTime:"+videoPlayTime);
                                    }
                                    if (imagePlayTime>0&&videoPlayTime<=0){
                                        SharedPreferencesUtil.setBooleanValue(mcontext, "isPicture", true);
                                        LogUtil.i(TAG, "StartAdvert: 播放图片 imagePlayTime:"+imagePlayTime);
                                    }
                                   advertCallBack.onSuccess(updateDataBean);
                                    break;
                                case 10:
                                    LogUtil.i(TAG, "onSuccess: get tempType is 10 ,next time play video");
                                    SharedPreferencesUtil.setBooleanValue(mcontext, "isVideoStream", true);
                                    advertCallBack.onSuccess(updateDataBean);
                                    break;
                                case 99:
                                default:
                                    advertCallBack.onError();
                                    break;
                            }
                        }
                    } catch (Exception e) {
                        LogUtil.i(TAG, "initAdvert onSuccess: get json data failed");
                        e.printStackTrace();
                        advertCallBack.onError();
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "initAdvert onError:");
                    advertCallBack.onError();
                }
            });
        }catch (NullPointerException e){
            e.printStackTrace();
            advertCallBack.onError();
        }

    }
    public static void uploadAdverLog(final DeviceInfoBean deviceInfoBean,Context mcontext,String videoUrl) {
        try {
            JSONObject json = new JSONObject();
            json.put("userName", deviceInfoBean.getUsername());
            json.put("version_code", Utils.getVersionCode(mcontext));
            json.put("version_name", Utils.getVersionName(mcontext));
            json.put("Area_id", deviceInfoBean.getArea_id());
            json.put("stbModle", Build.MODEL);
            json.put("EPGGroupNMB", deviceInfoBean.getEPGGroupNMB());
            json.put("Group_id", deviceInfoBean.getGroup_id());
            json.put("stb_id", deviceInfoBean.getStb_id());
            json.put("videoUrl", videoUrl);
            LogUtil.i(TAG, "uploadAdverLog: request json:" + json.toString());
            PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL + URL_Manager.ADVERTISEMENT_UPLOADLOG_URL).tag(TAG);
            postRequest.params("json", json.toString());
            postRequest.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    LogUtil.i(TAG, "onSuccess: upload success:"+ response.body());
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "onError: upload log failed,request error");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
