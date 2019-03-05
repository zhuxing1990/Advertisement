package com.vunke.chinaunicom.advertisement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.vunke.chinaunicom.advertisement.R;
import com.vunke.chinaunicom.advertisement.base.BaseActivity;
import com.vunke.chinaunicom.advertisement.listener.WebUtils;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.utils.GroupStategyUtils;

import org.json.JSONObject;

public class NotifyActivity extends BaseActivity {
    private static final String TAG = "NotifyActivity";
    private WebView notfy_webView;
    private RelativeLayout notify_layout;
    private long endtime;
    private long starttime;
    private long timestamp;
    private int push_id;
    private int strategy_id;
    private int push_type;
    private int behavior_type;
    private String path;
    private String version_code;

    private String keyvalue = "113";
    private String getVersion_vode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate: ");
        setContentView(R.layout.activity_notify);
        initDeviceInfo();
        getIntentData();
        initView();
    }
    public void getIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra("path")){
            path = intent.getStringExtra("path");
            initCountDownTimer(10);
        }
        if (TextUtils.isEmpty(path)){
            startEPG();
        }
    }
    private DeviceInfoBean deviceInfoBean;
    private void initDeviceInfo() {
        deviceInfoBean = new DeviceInfoBean();
        DevicesManager.queryDevicesInfo(mcontext,deviceInfoBean);
        LogUtil.i(TAG, "initDeviceInfo: deviiceInfoBean:"+deviceInfoBean.toString());
    }
    /**
     * 初始化 webView
     */
    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    private void initView() {
        notify_layout = (RelativeLayout) findViewById(R.id.notify_layout);
        notfy_webView = (WebView) findViewById(R.id.notfy_webView);
        WebUtils.SetWebView(notfy_webView,mcontext);

        notfy_webView.addJavascriptInterface(new JavaScriptObject(),
                "advert");
        notfy_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.i(TAG, "网页加载中");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.i(TAG, "网页加载结束");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notfy_webView.setVisibility(View.VISIBLE);
                        notfy_webView.requestFocus();
                    }
                }, 200);

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Toast.makeText(mcontext, url, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "shouldOverrideUrlLoading: url:"+url);
                view.loadUrl(url);//在2.3上面不加这句话，可以加载出页面，在4.0上面必须要加入，不然出现白屏
                return true;
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
               startEPG();
            }
        });
        notfy_webView.setWebChromeClient(new WebChromeClient(){
            /**
             * 当WebView加载之后，返回 HTML 页面的标题 Title
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                if(!TextUtils.isEmpty(title)&&
                        title.toLowerCase().contains("error")||
                        title.toLowerCase().contains("400")||
                        title.toLowerCase().contains("404")||
                        title.toLowerCase().contains("500")||
                        title.toLowerCase().contains("502")||
                        title.toLowerCase().contains("503")){
                    startEPG();
                }
            }
        });
        try {
            String postData = "userName=" + deviceInfoBean.getUsername()+"&userToken="+deviceInfoBean.getUser_token()+"&stb_id="+deviceInfoBean.getStb_id();
//            notfy_webView.postUrl(path,postData.getBytes());
            notfy_webView.loadUrl(path+postData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private CountDownTimer countDownTimer;
    private void cancelCountDownTimer() {
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void initCountDownTimer(int closeTime) {
        LogUtil.i(TAG, "initCountDownTimer: ");
        cancelCountDownTimer();
        if (closeTime<=0){
            closeTime = 10;
        }
        countDownTimer = new CountDownTimer(closeTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.i(TAG, "onTick: CountDown" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                LogUtil.i(TAG, "CountDownTimer1 onFinish: ");
                startEPG();
            }
        }.start();
    }
    /**
     * 安卓与JS交互
     *
     * @author zhuxi
     */
    public class JavaScriptObject {
        @JavascriptInterface
        public void sendMessageToJAVA(String json) {
            LogUtil.i(TAG, "getJavaScript:" + json);
            // Toast.makeText(getApplicationContext(), json,
            // Toast.LENGTH_SHORT).show();
            if (json.equals("0")) {
                LogUtil.i(TAG, "用户没有操作");
                startEPG();
            } else if (json.equals("1")) {
                LogUtil.i(TAG, "用户正在操作");
            }
        }
        @JavascriptInterface
        public void initWebTime(String json){
            try {
                JSONObject jsonData = new JSONObject(json);
                if (jsonData.has("initWebTime")){
                    int time = jsonData.getInt("initWebTime");
                    cancelCountDownTimer();
                    initCountDownTimer(time);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void startEPG() {
        LogUtil.i(TAG, "startEPG: ");
        GroupStategyUtils.StartEPG(mcontext,deviceInfoBean);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.i(TAG, "keyCode:" + keyCode);
	/*	if ((keyCode == KeyEvent.KEYCODE_BACK)) {// && notfy_webView.canGoBack()
			// notfy_webView.goBack(); // goBack()表示返回WebView的上一页面
			StartEPGing();
			return true;
		}*/
        if ((keyCode == KeyEvent.KEYCODE_BACK) && notfy_webView.canGoBack()) {//尚未测试
            goBack();
            if (notfy_webView.getUrl().contains(path)) {
                startEPG();
                return false;
            } else {
                notfy_webView.goBack(); // goBack()表示返回WebView的上一页面
                return true;
            }
        }else if ((keyCode == KeyEvent.KEYCODE_BACK)) {// && notfy_webView.canGoBack()
            goBack();
            startEPG();
            // notfy_webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            LogUtil.i(TAG, "用户按HOME键");
            startEPG();
        }
        return super.onKeyDown(keyCode, event);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG,"NotifyActivity onDestory");
        cancelCountDownTimer();
    }
    public void goBack(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            notfy_webView.evaluateJavascript("javascript:goBack()", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //此处为 js 返回的结果
                }
            });
        }else{
            notfy_webView.loadUrl("javascript:goBack()");
        }
    }
}
