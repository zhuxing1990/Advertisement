package com.vunke.chinaunicom.advertisement.service;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
import com.vunke.chinaunicom.advertisement.manager.DownloadManager;
import com.vunke.chinaunicom.advertisement.manager.URL_Manager;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.modle.UpdateDataBean;
import com.vunke.chinaunicom.advertisement.utils.Utils;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class DownLoadService extends Service {
	private static final String TAG = "DownLoadService";

	@Override
	public void onCreate() {
		LogUtil.i(TAG, "DownLoadService onCreate: ");
		initDeviceInfo();
	}

	private DeviceInfoBean deviceInfoBean;
	private void initDeviceInfo() {
		deviceInfoBean = new DeviceInfoBean();
		DevicesManager.queryDevicesInfo(getApplicationContext(),deviceInfoBean);
		LogUtil.i(TAG, "initDeviceInfo: deviiceInfoBean:"+deviceInfoBean.toString());
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.i(TAG, "Downloadservice onStartCommand");
			getUpdateData();
		return super.onStartCommand(intent, flags, startId);
	}

	private void getUpdateData() {
		LogUtil.i(TAG, "getUpdateData: ");
		try {
			OkHttpClient ok = new OkHttpClient();
			ok.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
			PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL+URL_Manager.ADVERTISEMENT_IPTVSTRATEGY_URL).tag(this).retryCount(1).client(ok);
			JSONObject json = new JSONObject();
			json.put("userName",deviceInfoBean.getUsername());
			json.put("version_code", Utils.getVersionCode(getApplicationContext()));
			json.put("version_name",Utils.getVersionName(getApplicationContext()));
			json.put("Area_id",deviceInfoBean.getArea_id());
			json.put("stbModle", Build.MODEL);
			json.put("EPGGroupNMB",deviceInfoBean.getEPGGroupNMB());
			json.put("Group_id",deviceInfoBean.getGroup_id());
			json.put("stb_id",deviceInfoBean.getStb_id());
			LogUtil.i(TAG, "getUpdateData: request:json ="+json.toString());
			postRequest.params("json", json.toString());
			postRequest.execute(new StringCallback() {
				@Override
				public void onSuccess(Response<String> response) {
					String s = response.body();
					if (!TextUtils.isEmpty(s)) {
						LogUtil.i(TAG, "getUpdateData onSuccess: s"+s);
						try {
							UpdateDataBean updateDataBean = new Gson().fromJson(s, UpdateDataBean.class);
							if (updateDataBean!=null){
								DownloadManager downloadManager = new DownloadManager(getApplicationContext());
								downloadManager.GetUpdateData(getApplicationContext(),updateDataBean);
							}else{
								LogUtil.i(TAG, "GetUpdateData: get data is null,update over");
							}
						} catch (Exception e) {
							LogUtil.i(TAG, "getUpdateData onSuccess: get json data failed");
							e.printStackTrace();
						}
					}else{
						LogUtil.i(TAG, "getUpdateData onSuccess: get data is null");
					}
				}

				@Override
				public void onError(Response<String> response) {
					super.onError(response);
					LogUtil.i(TAG, "getUpdateData onError:");
				}

				@Override
				public void onFinish() {
					super.onFinish();
					LogUtil.i(TAG, "getUpdateData onFinish: ");
					stopSelf();
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}

	}


	@Override
	public IBinder onBind(Intent arg0) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "DownLoadService--ondestory--");
	};

           
}
