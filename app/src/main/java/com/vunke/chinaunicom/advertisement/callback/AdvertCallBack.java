package com.vunke.chinaunicom.advertisement.callback;

import com.vunke.chinaunicom.advertisement.modle.UpdateDataBean;

public interface AdvertCallBack{
     void onSuccess(UpdateDataBean updateDataBean);
     void onError();
}