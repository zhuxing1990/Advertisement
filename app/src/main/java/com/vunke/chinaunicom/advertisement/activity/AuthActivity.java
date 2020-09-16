package com.vunke.chinaunicom.advertisement.activity;

import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.widget.TextView;

import com.vunke.chinaunicom.advertisement.R;
import com.vunke.chinaunicom.advertisement.base.BaseActivity;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.utils.GroupStategyUtils;
import com.vunke.chinaunicom.advertisement.utils.SharedPreferencesUtil;
import com.vunke.chinaunicom.advertisement.utils.Utils;

public class AuthActivity extends BaseActivity {
    private static final String TAG = "AuthActivity";
    private TextView auth_version;
    private String versionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth_version = (TextView) findViewById(R.id.auth_version);
        versionName = Utils.getVersionName(mcontext);
        auth_version.setText(TextUtils.isEmpty(versionName)?"": versionName);
        initAuth();
    }
    private void initAuth() {
        DeviceInfoBean deviceInfoBean = new DeviceInfoBean();
        DevicesManager.queryDevicesInfo(mcontext,deviceInfoBean);
        LogUtil.i(TAG, "initAuth: deviceInfoBean:"+deviceInfoBean.toString());
        if (!TextUtils.isEmpty(deviceInfoBean.getLoginstatus())){
             switch (deviceInfoBean.getLoginstatus()){
                 case "ok":
                     GroupStategyUtils.StartEPG(mcontext,deviceInfoBean);
                     break;
                 case "notlogin":
                 case "loading":
                    default:
                        startEPG("com.ai.nsg.iptv.stbauthapp");
                        finish();
                        android.os.Process.killProcess(Process.myPid());
                        break;
             }
        }else{
            GroupStategyUtils.StartMangGuoEPG(mcontext);
        }
    }
    private void startEPG(String packageName) {
        LogUtil.i(TAG, "startEPG: ");
        if (Utils.isPkgInstalled(mcontext,packageName)){
            Utils.StartEPG(packageName,mcontext);
            SharedPreferencesUtil.setBooleanValue(mcontext,
                    SharedPreferencesUtil.IS_PALYED_ADVERT, true);
            finish();
        }else{
            GroupStategyUtils.StartMangGuoEPG(mcontext);
        }
    }
}
