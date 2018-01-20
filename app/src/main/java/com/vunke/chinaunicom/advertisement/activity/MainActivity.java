package com.vunke.chinaunicom.advertisement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;
import com.vunke.chinaunicom.advertisement.R;
import com.vunke.chinaunicom.advertisement.base.BaseActivity;
import com.vunke.chinaunicom.advertisement.listener.WebUtils;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
import com.vunke.chinaunicom.advertisement.manager.DownloadManager;
import com.vunke.chinaunicom.advertisement.manager.FileManager;
import com.vunke.chinaunicom.advertisement.manager.URL_Manager;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.modle.NotifyBean;
import com.vunke.chinaunicom.advertisement.modle.PlayVideoBean;
import com.vunke.chinaunicom.advertisement.modle.UpdateDataBean;
import com.vunke.chinaunicom.advertisement.utils.DensityUtil;
import com.vunke.chinaunicom.advertisement.utils.GroupStategyUtils;
import com.vunke.chinaunicom.advertisement.utils.SharedPreferencesUtil;
import com.vunke.chinaunicom.advertisement.utils.Utils;

import org.json.JSONObject;

import java.util.List;

import javax.microedition.lcdui.view.LoadingView;
import javax.microedition.lcdui.view.VideoWindowSurfaceView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private CountDownTimer countDownTimer;
    public static int sw = 1280;
    public static int sh = 720;
    private WebView main_webView;

    public static FrameLayout framelayout;
    public static boolean isPlay = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "onCreate: ");
        SharedPreferencesUtil.setBooleanValue(mcontext,
                SharedPreferencesUtil.IS_PALYED_ADVERT, false);
        setWindows();
        initView();
        initDeviceInfo();
        initWebView();
        initAdvert();
        getVideo();
        initPush();
    }
    private void setWindows() {
        LogUtil.i(TAG, "setWindows: ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    }
    private void initView() {
        LogUtil.i(TAG, "initView: ");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        sw = dm.widthPixels;
        sh = dm.heightPixels;
        main_webView = new WebView(mcontext);
        main_webView.setVisibility(View.INVISIBLE);
        main_webView.requestFocus();
        ViewGroup.LayoutParams framelayout_params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        framelayout = new FrameLayout(this);
        framelayout.setBackgroundResource(R.drawable.bg_logo);
        framelayout.setLayoutParams(framelayout_params);
        setContentView(framelayout);
    }
    /**
     * 初始化 webView
     */
    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    private void initWebView() {
        LogUtil.i(TAG, "initWebView: ");
        main_webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        main_webView.setBackgroundColor(Color.parseColor("#000000"));
        WebUtils.SetWebView(main_webView,mcontext);
        main_webView.addJavascriptInterface(new JavaScriptObject(),
                "video_advert");
        main_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                main_webView.requestFocus();
                LogUtil.i(TAG, "网页加载中");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                main_webView.requestFocus();
                LogUtil.i(TAG, "网页加载结束");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        main_webView.setVisibility(View.VISIBLE);
                    }
                }, 200);
            }
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (main_webView!=null){
                    framelayout.removeView(main_webView);
                }
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.i(TAG, "shouldOverrideUrlLoading: url"+url);
                if (startPlay){
                    view.loadUrl(url);
                    return true;
                }
                if (updateDataBean!=null&&!url.equals(updateDataBean.getJson().getTemplateUrl())) {
                    videoview.close();
                    videoview.setVisibility(View.INVISIBLE);
                } else {
                    videoview.play();
                    videoview.setVisibility(View.VISIBLE);
                }
                view.loadUrl(url);
                return true;
            }
        });
        main_webView.setWebChromeClient(new WebChromeClient(){
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
                    framelayout.removeView(main_webView);
                }
            }
        });
        float scanW = (float) sw / (float) 1280;
        float scanH = (float) sh / (float) 720;
        main_webView.setInitialScale((int) (scanW * 100));
        framelayout.addView(main_webView);
        setContentView(framelayout);
//        progressBar = new ProgressBar(mcontext);
//        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,  FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER); // set size
//        progressBar.setLayoutParams(frameParams);
//        progressBar.bringToFront();
//        framelayout.addView(progressBar);
    }

    private DeviceInfoBean deviceInfoBean;
    private void initDeviceInfo() {
        deviceInfoBean = new DeviceInfoBean();
        DevicesManager.queryDevicesInfo(mcontext,deviceInfoBean);
        LogUtil.i(TAG, "initDeviceInfo: deviiceInfoBean:"+deviceInfoBean.toString());
    }
    private   UpdateDataBean updateDataBean;
    private void initAdvert() {
        try {
//            OkHttpClient ok = new OkHttpClient();
//            ok.newBuilder().connectTimeout(10, TimeUnit.SECONDS);
            PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL+URL_Manager.ADVERTISEMENT_IPTVSTRATEGY_URL).tag(this).retryCount(1);
//            postRequest.client(ok);
            JSONObject json = new JSONObject();
            json.put("userName",deviceInfoBean.getUsername());
            json.put("version_code",Utils.getVersionCode(mcontext));
            json.put("version_name",Utils.getVersionName(mcontext));
            json.put("Area_id",deviceInfoBean.getArea_id());
            json.put("stbModle", Build.MODEL);
            json.put("EPGGroupNMB",deviceInfoBean.getEPGGroupNMB());
            json.put("Group_id",deviceInfoBean.getGroup_id());
            json.put("stb_id",deviceInfoBean.getStb_id());
            LogUtil.i(TAG, "initAdvert: request: json="+json.toString());
            postRequest.params("json", json.toString());
            postRequest.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    String s = response.body();
                    if (!TextUtils.isEmpty(s)) {
                        LogUtil.i(TAG, "initAdvert onSuccess: s:"+s);
                        try {
                             updateDataBean = new Gson().fromJson(s, UpdateDataBean.class);
                            if (updateDataBean!=null&&!TextUtils.isEmpty(updateDataBean.getCode())){
                                   if (updateDataBean.getCode().equals("200")){
                                       LogUtil.i(TAG, "onSuccess: get code =200");
                                       DownloadManager downloadManager = new DownloadManager(mcontext);
                                        downloadManager.GetUpdateData(mcontext,updateDataBean);
                                        int templateTYPE = MainActivity.this.updateDataBean.getJson().getTemplateTYPE();
                                        LogUtil.i(TAG, "initAdvert onSuccess: templateTYPE:"+templateTYPE);
                                        switch (templateTYPE){
                                            case 1:
                                                setData();
                                                break;
                                            case 99:
                                            default:
                                                   initCountDownTimer();
                                                break;
                                        }
                                   }else{
                                       LogUtil.i(TAG, "onSuccess: get code :"+updateDataBean.getCode());
                                   }
//

                            }else{
                                LogUtil.i(TAG, "initAdvert: get data is null,update over");
                                initCountDownTimer();
                            }
                        } catch (Exception e) {
                            LogUtil.i(TAG, "initAdvert onSuccess: get json data failed");
                            e.printStackTrace();
                            initCountDownTimer();
                        }
                    }else{
                        LogUtil.i(TAG, "initAdvert onSuccess: get data is null");
                        initCountDownTimer();
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "initAdvert onError:");
                    initCountDownTimer();
                }

            });
        }catch (Exception e){
            e.printStackTrace();
            initCountDownTimer();
        }
    }

    private void setData() {
       try {
           int imagePlayTime = updateDataBean.getJson().getImagePlayTime();
           int videoPlayTime = updateDataBean.getJson().getVideoPlayTime();
           if (videoPlayTime>0&&imagePlayTime>0){
               LogUtil.i(TAG, "StartAdvert: 播放视频 videoPlayTime:"+videoPlayTime);
           }else if (videoPlayTime>0&&imagePlayTime<=0){
               LogUtil.i(TAG, "StartAdvert: 播放视频 videoPlayTime:"+videoPlayTime);
           }else if (imagePlayTime>0&&videoPlayTime<=0){
               SharedPreferencesUtil.setIntValue(mcontext,"imagePlayTime",imagePlayTime);
               LogUtil.i(TAG, "StartAdvert: 播放图片 imagePlayTime:"+imagePlayTime);
               cancelCountDownTimer();
               Utils.startPictureActivity(mcontext);
               return;
           }else {
               LogUtil.i(TAG, "StartAdvert: 默认播放视频");
           }
           if (updateDataBean.getJson().getVideoPlayTime()>0){
               initCloseTime(updateDataBean.getJson().getVideoPlayTime());
               if (TextUtils.isEmpty(updateDataBean.getJson().getTemplateUrl())){
                   LogUtil.i(TAG, "setData: get templateUrl is null");
                   return;
               }
               String postData = "userName="+deviceInfoBean.getUsername();
               main_webView.postUrl(updateDataBean.getJson().getTemplateUrl(),postData.getBytes());
           }else{
               initCountDownTimer();
           }
       }catch (Exception e){
           e.printStackTrace();
           initCountDownTimer();
       }
    }
    private NotifyBean notifyBean;
    public void initPush(){
        try {
            JSONObject json = new JSONObject();
            json.put("userName",deviceInfoBean.getUsername());
            json.put("version_code",Utils.getVersionCode(mcontext));
            json.put("version_name",Utils.getVersionName(mcontext));
            LogUtil.i(TAG, "initPush: request json:"+json.toString());
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
    private void getVideo() {
        LogUtil.i(TAG, "getVideo: ");
        Observable.unsafeCreate(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                LogUtil.i(TAG, "start getVideo: ");
               List<String>  VideoList = FileManager.initVideoFile(mcontext);
                if (VideoList!=null){
                    subscriber.onNext(VideoList);

                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
         .subscribe(new Subscriber<List<String>>() {
             @Override
             public void onCompleted() {
                 LogUtil.i(TAG, "onCompleted: getVideo");
             }

             @Override
             public void onError(Throwable e) {
                 LogUtil.i(TAG, "get video onError: ");
                this.unsubscribe();
                 e.printStackTrace();
                 initCountDownTimer();
             }

             @Override
             public void onNext(List<String> videoList) {
                 if (videoList!=null){
                     LogUtil.i(TAG, "onNext: getVideo");
                    setVideo(videoList.get(0).toString().trim());
                 }else{
                     LogUtil.i(TAG, "getVideo: get videoFile failed");
                     initCountDownTimer();
                     this.unsubscribe();
                 }
             }
         });
    }


    public static VideoWindowSurfaceView videoview = null;
//    public static ProgressBar progressBar;
    private static LoadingView laodingView;

    private void setVideo(String videoUrl) {
        LogUtil.i(TAG, "setVideo: ");
        removVideoView();
//        videoUrl = videoUrl + "&window_x=0&window_y=0&window_w=1280&window_h=720&windowmode=9";
        LogUtil.i(TAG, "setVideo: videoUrl = " + videoUrl);
        videoview = new VideoWindowSurfaceView(mcontext, videoUrl);
        isPlay = true;
        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT); // set size
        videoview.setLayoutParams(cameraFL);
        framelayout.addView(videoview);
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
    public static TextView closeTimeText = null;
    private long CloseTime = 0;

    /**
     * 初始化 关闭时间
     *
     * @param close_time
     */
    private void initCloseTime(final long close_time) {
        LogUtil.i(TAG, "initCloseTime: close_time:"+close_time);
        CloseTime = close_time;
        int templateTYPE = updateDataBean.getJson().getTemplateTYPE();
        LogUtil.i(TAG, "initCloseTime: tempileId:" + templateTYPE);
        if (templateTYPE == 1) {
            removeTextView();
            closeTimeText = new TextView(mcontext);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
            layoutParams.setMargins(0, 10, 10, 0);
            closeTimeText.setLayoutParams(layoutParams);
            closeTimeText.setTextSize(DensityUtil.px2sp(mcontext, 28));
            closeTimeText.setTextColor(Color.parseColor("#FF0000"));
            closeTimeText.bringToFront();
            framelayout.addView(closeTimeText);
            LogUtil.i(TAG, "initCloseTime: addTextView");
            closeTimeText.setText(CloseTime + "");
        }
        LogUtil.i(TAG, "initCloseTime: :" + close_time);
        cancelCountDownTimer();
        countDownTimer = new CountDownTimer(CloseTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (closeTimeText != null) {
                    closeTimeText.setText(millisUntilFinished / 1000 + "");
                    LogUtil.i(TAG, "onTick: CloseTime:" + millisUntilFinished / 1000);
                }
            }

            @Override
            public void onFinish() {
                LogUtil.i(TAG, "CountDownTimer onFinish: ");
                if(isCancel){
                    isCancel = false;
                }else{
                    startEPG();
                }
            }
        }.start();
    }


    private void initCountDownTimer() {
        LogUtil.i(TAG, "initCountDownTimer: ");
        cancelCountDownTimer();
        int closeTime = 15;
        countDownTimer = new CountDownTimer(closeTime*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.i(TAG, "onTick: CountDown"+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                LogUtil.i(TAG, "CountDownTimer onFinish: ");
                if(isCancel){
                    isCancel = false;
                }else{
                    startEPG();
                }

            }
        };
        countDownTimer.start();
    }
    private boolean isCancel = false;
    private void cancelCountDownTimer() {
        if (countDownTimer!=null){
            isCancel = true;
            countDownTimer.cancel();
        }
    }

    /**
     * 继续播放：在页面的onResume方法中使用
     */
    private boolean startPlay = true;
    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "Notify2Activity onResume: ");
        if(startPlay){
            startPlay = false;
            return;
        }
    }

    /**
     * 安卓与JS交互
     *
     * @author zhuxi
     */
    public class JavaScriptObject {
        @JavascriptInterface
        public void sendMessageToJAVA(final String json) {
            try {
                PlayVideoBean playVideoBean = new Gson().fromJson(json,PlayVideoBean.class);
                Observable.create(new Observable.OnSubscribe<PlayVideoBean>() {
                    @Override
                    public void call(Subscriber<? super PlayVideoBean> subscriber) {
                        LogUtil.i(TAG, "sendMessageToJAVA: "+json);
                        PlayVideoBean videoBean = new Gson().fromJson(json,PlayVideoBean.class);
                        subscriber.onNext(videoBean);
                    }

                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<PlayVideoBean>() {
                            @Override
                            public void onCompleted() {
                                this.unsubscribe();
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                this.unsubscribe();
                            }
                            @Override
                            public void onNext(final PlayVideoBean playVideoBean) {
                                if (playVideoBean!=null){
                                    SetWebVideo(playVideoBean);
                                    if (playVideoBean.getClose_time()!=0){
                                        initCloseTime(playVideoBean.getClose_time());
                                    }
                                }
                            }
                        }) ;

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private void SetWebVideo(PlayVideoBean playVideoBean) {
        LogUtil.i(TAG, "SetWebVideo: ");
        if (!TextUtils.isEmpty(playVideoBean.getPlay_url())) {
            LogUtil.i(TAG, "onNext: play_url:" + playVideoBean.getPlay_url());
            videoview.setNextVideo(playVideoBean.getPlay_url());
            float scanW = (float) sw / (float) 1280;
            // float scanH = (float) sh / (float) 720;
            int x = (int) (Integer.valueOf(playVideoBean.getMarginLeft()).intValue() * scanW);
            int y = (int) (Integer.valueOf(playVideoBean.getMarginTop()).intValue() * scanW);
            int w = (int) (Integer.valueOf(playVideoBean.getWidth()).intValue() * scanW);
            int h = (int) (Integer.valueOf(playVideoBean.getHeight()).intValue() * scanW);
            FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                    w, h, Gravity.TOP); // set size
            cameraFL.setMargins(x, y, 0, 0); // set position
            videoview.setLayoutParams(cameraFL);
            videoview.setLoop(playVideoBean.isIs_loop());
        }else{
            LogUtil.i(TAG, "SetWebVideo: get video url is null");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "MainActivity onDestroy: ");
        if (videoview != null) {
            videoview.close();
        }
        System.gc();
        cancelCountDownTimer();
    }

    public void removeTextView() {
        LogUtil.i(TAG, "removeTextView: ");
        if (closeTimeText != null) {
            framelayout.removeView(closeTimeText);
            closeTimeText = null;
        }
    }

    public static void removVideoView() {
        LogUtil.i(TAG, "removVideoView: ");
        if (laodingView != null) {
            framelayout.removeView(laodingView);
            laodingView = null;
        }
        if (videoview != null) {
            framelayout.removeView(videoview);
        }
    }

    public static void removLoadingView() {
        LogUtil.i(TAG, "removLoadingView: ");
        if (laodingView != null) {
            framelayout.removeView(laodingView);
            laodingView = null;
        }

//        if (progressBar != null) {
//            framelayout.removeView(progressBar);
//            progressBar = null;
//        }
    }



}
