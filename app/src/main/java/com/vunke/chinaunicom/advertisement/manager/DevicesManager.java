package com.vunke.chinaunicom.advertisement.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhuxi on 2018/1/3.
 */
public class DevicesManager {
    private static final String TAG = "DevicesManager";

    public static void queryDevicesInfo(Context context, DeviceInfoBean deviceInfoBean) {
        String baseURL = "content://stbconfig/authentication/";
        Uri localUri = Uri.parse(baseURL);
        Cursor cursor = context.getContentResolver().query(localUri,
                null, null, null, null);
        try {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String value = cursor.getString(cursor.getColumnIndex("value"));
//                    LogUtil.i(TAG, "queryDevicesInfo: " + name + "=" + value);
                    SetDeviceInfo(name,value,deviceInfoBean);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private static void SetDeviceInfo(final String name ,final String value,final DeviceInfoBean deviceInfoBean) {
        Observable.just(name)
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return !TextUtils.isEmpty(s);
                    }
                })
                 .observeOn(Schedulers.io())
                 .subscribe(new Subscriber<String>() {
                     @Override
                     public void onCompleted() {
                         this.unsubscribe();
                     }

                     @Override
                     public void onError(Throwable e) {
                         LogUtil.i(TAG, "onError: ");
                        e.printStackTrace();
                         this.unsubscribe();
                     }

                     @Override
                     public void onNext(String s) {
                         LogUtil.d(TAG, "onNext: "+s+"="+value);
                         switch (s) {
                             case "username":
                                 deviceInfoBean.setUsername(value);
                                 break;
                             case "password":
                                 deviceInfoBean.setPassword(value);
                                 break;
                             case "user_token":
                                 deviceInfoBean.setUser_token(value);
                                 break;
                             case "auth_server":
                                 deviceInfoBean.setAuth_server(value);
                                 break;
                             case "loginstatus":
                                 deviceInfoBean.setLoginstatus(value);
                                 break;
                             case "stb_id":
                                 deviceInfoBean.setStb_id(value);
                                 break;
                             case "EPGDomain":
                                 deviceInfoBean.setEPGDomain(value);
                                 break;
                             case "EPGGroupNMB":
                                 deviceInfoBean.setEPGGroupNMB(value);
                                 break;
                             case "Area_id":
                                 deviceInfoBean.setArea_id(value);
                                 break;
                             case "Group_id":
                                 deviceInfoBean.setGroup_id(value);
                                 break;
                             default:
                                 break;
                         }
                     }
                 });

    }
}
