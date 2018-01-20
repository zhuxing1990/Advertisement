package com.vunke.chinaunicom.advertisement.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.service.DownLoadService;
import com.vunke.chinaunicom.advertisement.utils.Utils;

public class ConnectionChangeReceiver extends BroadcastReceiver {
	private static final String TAG = ConnectionChangeReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Utils.isNetworkAvailable(context)) {
			LogUtil.i(TAG, "网络未连接 ----------");
		}

		if (!Utils.isNetworkConnected(context)) {
			LogUtil.i(TAG, "网络还未连接----------");
		}
		if (Utils.isNetConnected(context)) {
			LogUtil.i(TAG, "网络已连接----------");
			Intent it = new Intent(context,DownLoadService.class);
			context.startService(it);
		}
	}

}










