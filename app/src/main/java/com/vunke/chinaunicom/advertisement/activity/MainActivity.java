package com.vunke.chinaunicom.advertisement.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
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
import com.vunke.chinaunicom.advertisement.callback.AdvertCallBack;
import com.vunke.chinaunicom.advertisement.listener.WebUtils;
import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.manager.AdvertManage;
import com.vunke.chinaunicom.advertisement.manager.DevicesManager;
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
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private static final String TAG = "MainActivity";
    private FrameLayout main_frame;
    private WebView main_webView;
    public static int sw = 1280;
    public static int sh = 720;
    private boolean isOnCreate = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isOnCreate = true;
        boolean isPicture = SharedPreferencesUtil.getBooleanValue(mcontext, "isPicture", false);
        LogUtil.i(TAG, "onCreate: isPicture:"+isPicture);
        if (isPicture) {
            Utils.startPictureActivity(mcontext);
            return;
        }
        initView();
        AddSurfaceView();
        initDeviceInfo();
        initWebView();
        initCountDownTimer();
        initAdvert();
        boolean isVideoStream = SharedPreferencesUtil.getBooleanValue(mcontext,"isVideoStream",false);
        LogUtil.i(TAG, "onCreate: isVideoStream:"+isVideoStream);
        if (!isVideoStream){
            getVideo();
        }
        initPush();
//        showToast("当前版本:"+Utils.getVersionName(mcontext));
    }

    private void initView() {
        main_frame = (FrameLayout) findViewById(R.id.main_frame);
        main_webView = new WebView(mcontext);
        main_webView.setVisibility(View.INVISIBLE);
    }

    private DeviceInfoBean deviceInfoBean;

    private void initDeviceInfo() {
        deviceInfoBean = new DeviceInfoBean();
        DevicesManager.queryDevicesInfo(mcontext, deviceInfoBean);
        LogUtil.i(TAG, "initDeviceInfo: deviiceInfoBean:" + deviceInfoBean.toString());
    }

    /**
     * 初始化 webView
     */
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void initWebView() {
        LogUtil.i(TAG, "initWebView: ");
        main_webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        main_webView.setBackgroundColor(Color.parseColor("#000000"));
        WebUtils.SetWebView(main_webView, mcontext);
        main_webView.addJavascriptInterface(new JavaScriptObject(),
                "video_advert");
        main_webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                main_webView.requestFocus();
                LogUtil.i(TAG, "网页加载中");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
//                main_webView.requestFocus();
                LogUtil.i(TAG, "网页加载结束");
                Observable.interval(200, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        main_webView.setVisibility(View.VISIBLE);
                        unsubscribe();
                    }

                    @Override
                    public void onNext(Long aLong) {
                        onCompleted();
                        main_webView.setVisibility(View.VISIBLE);
                        main_webView.requestFocus();
                    }
                });
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (main_webView != null) {
                    main_frame.removeView(main_webView);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.i(TAG, "shouldOverrideUrlLoading: url" + url);
                if (startPlay) {
                    view.loadUrl(url);
                    return true;
                }
                if (!url.equals(templateUrl)) {
                    close();
                    surfaceView.setVisibility(View.INVISIBLE);
                    LogUtil.i(TAG, "shouldOverrideUrlLoading: videoview.setVisibility INVISIBLE");
                } else {
                    play();
                    surfaceView.setVisibility(View.VISIBLE);
                    LogUtil.i(TAG, "shouldOverrideUrlLoading: videoview.setVisibility VISIBLE");
                }
                view.loadUrl(url);
                return true;
            }
        });
        main_webView.setWebChromeClient(new WebChromeClient() {
            /**
             * 当WebView加载之后，返回 HTML 页面的标题 Title
             * @param view
             * @param title
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                //判断标题 title 中是否包含有“error”字段，如果包含“error”字段，则设置加载失败，显示加载失败的视图
                if (!TextUtils.isEmpty(title) &&
                        title.toLowerCase().contains("error") ||
                        title.toLowerCase().contains("400") ||
                        title.toLowerCase().contains("404") ||
                        title.toLowerCase().contains("500") ||
                        title.toLowerCase().contains("502") ||
                        title.toLowerCase().contains("503")) {
                    main_frame.removeView(main_webView);
                }
            }
        });
        float scanW = (float) sw / (float) 1280;
        float scanH = (float) sh / (float) 720;
        main_webView.setInitialScale((int) (scanW * 100));
        main_frame.addView(main_webView);
//        progressBar = new ProgressBar(mcontext);
//        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT,  FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER); // set size
//        progressBar.setLayoutParams(frameParams);
//        progressBar.bringToFront();
//        framelayout.addView(progressBar);
    }

    private String templateUrl = "";
    private int templateType = -1;
    private String downLoadVideo = "";
    private void initAdvert() {
        LogUtil.i(TAG, "initAdvert: ");
        try {
            JSONObject json = AdvertManage.setRequestParams(mcontext, deviceInfoBean);
            LogUtil.i(TAG, "initAdvert: json:"+json.toString());
            AdvertManage.GetAdvertData(mcontext, json, new AdvertCallBack() {
                @Override
                public void onSuccess(UpdateDataBean updateDataBean) {
                    if (updateDataBean == null) {
//                        initCountDownTimer();
                        return;
                    }
                    try {
                        templateType = updateDataBean.getJson().getTemplateTYPE();
                        downLoadVideo = updateDataBean.getJson().getVideoDownloadUrl();
                        if (templateType == 10){
                            Log.i(TAG, "onSuccess: get templateType is 10 ,start init video");
                            if (TextUtils.isEmpty(videoUrl)){
                                Log.i(TAG, "onSuccess: get videoUrl is null");
                                if (mediaPlayer!=null){
                                    Log.i(TAG, "onSuccess:  mediaPlayer is null");
                                    if (!mediaPlayer.isPlaying()){
                                        Log.i(TAG, "onSuccess:  mediaPlayer not play");
                                        String videoDownloadUrl = updateDataBean.getJson().getVideoDownloadUrl();
                                        setVideo(videoDownloadUrl);
                                    }
                                }else{
                                    Log.i(TAG, "onSuccess:  mediaPlay not null");
                                    String videoDownloadUrl = updateDataBean.getJson().getVideoDownloadUrl();
                                    setVideo(videoDownloadUrl);
                                }
                            }
                        }else{
                            if (!getVideo){
                                getVideo();
                            }
                        }
                        int imagePlayTime = updateDataBean.getJson().getImagePlayTime();
                        int videoPlayTime = updateDataBean.getJson().getVideoPlayTime();
                        SharedPreferencesUtil.setIntValue(mcontext, "imagePlayTime", imagePlayTime);
                        LogUtil.i(TAG, "StartAdvert: 播放图片 imagePlayTime:" + imagePlayTime);
                        if (imagePlayTime > 0 && videoPlayTime <= 0) {
//                            initCountDownTimer();
                            LogUtil.i(TAG, "onSuccess: next time show picture");
                            return;
                        }
                        if (videoPlayTime > 0) {
                            initCloseTime(updateDataBean.getJson().getVideoPlayTime());
                            if (TextUtils.isEmpty(updateDataBean.getJson().getTemplateUrl())) {
                                LogUtil.i(TAG, "setData: get templateUrl is null");
                                main_frame.removeView(main_webView);
                                return;
                            }
                            templateUrl = updateDataBean.getJson().getTemplateUrl();
                            if(!TextUtils.isEmpty(templateUrl)){
                                LogUtil.i(TAG, "onSuccess: getTemplateUrl:"+templateUrl);
                                String postData = "userName=" + deviceInfoBean.getUsername()+"&userToken="+deviceInfoBean.getUser_token()+"&stb_id="+deviceInfoBean.getStb_id();
                                main_webView.postUrl(templateUrl, postData.getBytes());
                            }
                        } else {
//                            initCountDownTimer();
                        }

                    } catch (NullPointerException e) {
                        e.printStackTrace();
//                        initCountDownTimer();
                    }
                }

                @Override
                public void onError() {
//                    initCountDownTimer();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
                PlayVideoBean playVideoBean = new Gson().fromJson(json, PlayVideoBean.class);
                Observable.create(new Observable.OnSubscribe<PlayVideoBean>() {
                    @Override
                    public void call(Subscriber<? super PlayVideoBean> subscriber) {
                        LogUtil.i(TAG, "sendMessageToJAVA: " + json);
                        PlayVideoBean videoBean = new Gson().fromJson(json, PlayVideoBean.class);
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
                                if (playVideoBean != null) {
                                    SetWebVideo(playVideoBean);
                                    if (playVideoBean.getClose_time() != 0) {
                                        initCloseTime(playVideoBean.getClose_time());
                                    }
                                }
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void SetWebVideo(PlayVideoBean playVideoBean) {
        LogUtil.i(TAG, "SetWebVideo: ");
        if (TextUtils.isEmpty(playVideoBean.getPlay_url())) {
            LogUtil.i(TAG, "SetWebVideo: get video url is null");
            return;
        }
        LogUtil.i(TAG, "onNext: play_url:" + playVideoBean.getPlay_url());
        setNextVideo(playVideoBean.getPlay_url());
        if(null!=mediaPlayer){
            mediaPlayer.setLooping(playVideoBean.isIs_loop());
        }
        float scanW = (float) sw / (float) 1280;
        // float scanH = (float) sh / (float) 720;
        int x = (int) (Integer.valueOf(playVideoBean.getMarginLeft()).intValue() * scanW);
        int y = (int) (Integer.valueOf(playVideoBean.getMarginTop()).intValue() * scanW);
        int w = (int) (Integer.valueOf(playVideoBean.getWidth()).intValue() * scanW);
        int h = (int) (Integer.valueOf(playVideoBean.getHeight()).intValue() * scanW);
        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                w, h, Gravity.TOP); // set size
        cameraFL.setMargins(x, y, 0, 0); // set position
        surfaceView.setLayoutParams(cameraFL);
    }

    private NotifyBean notifyBean;

    public void initPush() {
        try {
            JSONObject json = new JSONObject();
            json.put("userName", deviceInfoBean.getUsername());
            json.put("version_code", Utils.getVersionCode(mcontext));
            json.put("version_name", Utils.getVersionName(mcontext));
            json.put("Area_id",deviceInfoBean.getArea_id());
            json .put("stbModle", Build.MODEL);
            json.put("EPGGroupNMB",deviceInfoBean.getEPGGroupNMB());
            json.put("Group_id",deviceInfoBean.getGroup_id());
            json.put("stb_id",deviceInfoBean.getStb_id());
            LogUtil.i(TAG, "initPush: request json:" + json.toString());
            PostRequest<String> postRequest = OkGo.<String>post(URL_Manager.BASE_URL + URL_Manager.ADVERTISEMENT_PUSH_URL).tag(this);
            postRequest.params("json", json.toString());
            postRequest.execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        String s = response.body();
                        if (TextUtils.isEmpty(s)) {
                            LogUtil.i(TAG, "onSuccess: get push data is null");
                        } else {
                            LogUtil.i(TAG, "initPush onSuccess: s:" + s);
                            notifyBean = new Gson().fromJson(s, NotifyBean.class);
                            LogUtil.i(TAG, "onSuccess: notifyBean:" + notifyBean.toString());
                            if (notifyBean != null && !TextUtils.isEmpty(notifyBean.getCode())) {
                                if (notifyBean.getCode().equals("200")) {
                                    LogUtil.i(TAG, "onSuccess: get code =200");
                                    getPushData();
                                } else {
                                    LogUtil.i(TAG, "onSuccess: get code:" + notifyBean.getCode());
                                }
                            } else {
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                    LogUtil.i(TAG, "initPush onError: ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPushData() {
        LogUtil.i(TAG, "getPushData: ");
        try {
            if (notifyBean != null) {
                if (!TextUtils.isEmpty(notifyBean.getJson().getPush_url())) {
                    OkGo.<String>post(notifyBean.getJson().getPush_url()).tag(this).execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            LogUtil.i(TAG, "onSuccess: get push Data success");
                            hsPush = true;
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            LogUtil.i(TAG, "onSuccess: get push Data failed");
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private boolean getVideo = false;
    private void getVideo() {
        LogUtil.i(TAG, "getVideo: ");
        getVideo = true;
        Observable.unsafeCreate(new Observable.OnSubscribe<List<String>>() {
            @Override
            public void call(Subscriber<? super List<String>> subscriber) {
                LogUtil.i(TAG, "start getVideo: ");
                List<String> VideoList = FileManager.initVideoFile(mcontext);
                if (VideoList != null) {
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
                        if (videoList != null) {
                            LogUtil.i(TAG, "onNext: getVideo");
                            setVideo(videoList.get(0).toString().trim());
                        } else {
                            LogUtil.i(TAG, "getVideo: get videoFile failed");
                            initCountDownTimer();
                            this.unsubscribe();
                        }
                    }
                });
    }

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private String videoUrl = "";

    private void setVideo(String videoUrl) {
        this.videoUrl = videoUrl;
        LogUtil.i(TAG, "setVideo: ");
//        videoUrl = videoUrl + "&window_x=0&window_y=0&window_w=1280&window_h=720&windowmode=9";
        LogUtil.i(TAG, " videoUrl = " + videoUrl);
        try {
            if (isPlaying()) {
                LogUtil.i(TAG, "initVideo: mediaPlayer is playing,stopVideo and release mediaplayer");
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            } else {
                initMediaPlayer();
//                mediaPlayer.setLooping(true);
            }
            if (!TextUtils.isEmpty(videoUrl)) {
                mediaPlayer.setDataSource(mcontext, Uri.parse(videoUrl));
            } else {
                LogUtil.i(TAG, "setVideo: get videoUrl is null");
            }
            LogUtil.i(TAG, "initVideo: loading video");
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            showToast("加载视频错误！");
            if (null != mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer = null;
                if (isFirstError){
                    isFirstError = false;
                    setVideo(videoUrl);
                }
            }
        }

    }

    public void setNextVideo(String videoPath) {
        LogUtil.i(TAG, "setNextVideo: ");
        this.videoUrl = videoPath;
        LogUtil.i(TAG, " videoUrl = " + videoUrl);
        try {
            if (isPlaying()) {
                LogUtil.i(TAG, "setNextVideo: isPlaying:" + isPlaying());
                mediaPlayer.reset();
                mediaPlayer.setDataSource(mcontext, Uri.parse(videoPath));
                mediaPlayer.prepareAsync();
            } else {
                LogUtil.i(TAG, "setNextVideo: isPlaying:" + isPlaying());
                if (null != mediaPlayer) {
                    LogUtil.i(TAG, "mediaPlayer != null");
                    mediaPlayer.release();
                } else {
                    LogUtil.i(TAG, "mediaPlayer is null");
                    initMediaPlayer();
                }
                mediaPlayer.setDataSource(mcontext, Uri.parse(videoPath));
                mediaPlayer.prepareAsync();
                mediaPlayer.setLooping(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("加载视频错误！");
        }
    }

    private void AddSurfaceView() {
        if (surfaceView == null) {
            surfaceView = new SurfaceView(mcontext);
            FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT); // set size
            surfaceView.setLayoutParams(cameraFL);
            surfaceView.getHolder().addCallback(this);
            main_frame.addView(surfaceView);
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        // 设置声音效果
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置播放完成监听
        mediaPlayer.setOnCompletionListener(this);
        // 设置媒体加载完成以后回调函数。
        mediaPlayer.setOnPreparedListener(this);
        // 错误监听回调函数
        mediaPlayer.setOnErrorListener(this);
    }

    public boolean isPlaying() {
        if (null != mediaPlayer) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    private CountDownTimer countDownTimer;

    /**
     * 初始化 关闭时间
     *
     * @param close_time
     */
    private void initCloseTime(final long close_time) {
        LogUtil.i(TAG, "initCloseTime: close_time:" + close_time);
        CloseTime = close_time;
//        int templateTYPE = updateDataBean.getJson().getTemplateTYPE();
        LogUtil.i(TAG, "initCloseTime: tempileId:" + templateType);
        if (templateType == 1 || templateType == 10) {
            removeTextView();
            closeTimeText = new TextView(mcontext);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.RIGHT);
            layoutParams.setMargins(0, 10, 10, 0);
            closeTimeText.setLayoutParams(layoutParams);
            closeTimeText.setPadding(20,10,20,10);
            closeTimeText.setTextSize(DensityUtil.px2sp(mcontext, 28));
//            closeTimeText.setTextColor(Color.parseColor("#FF0000"));
            closeTimeText.setTextColor(Color.parseColor("#FFFFFF"));
            closeTimeText.setBackgroundColor(Color.parseColor("#40000000"));
            closeTimeText.bringToFront();
            main_frame.addView(closeTimeText);
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
                LogUtil.i(TAG, "CountDownTimer2 onFinish: ");
//                if(isCancel){
//                    isCancel = false;
//                }else{
                startEPG();
//                }
            }
        }.start();
    }


    private void initCountDownTimer() {
        LogUtil.i(TAG, "initCountDownTimer: ");
        cancelCountDownTimer();
        int closeTime = 15;
        countDownTimer = new CountDownTimer(closeTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.i(TAG, "onTick: CountDown:" + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                LogUtil.i(TAG, "CountDownTimer1 onFinish: ");
//                if(isCancel){
//                    isCancel = false;
//                }else{
                startEPG();
//                }

            }
        }.start();
    }

    //    private boolean isCancel = false;
    private void cancelCountDownTimer() {
        if (countDownTimer != null) {
//            isCancel = true;
            countDownTimer.cancel();
        }
    }

    private boolean hsPush = false;

    private void startEPG() {
        LogUtil.i(TAG, "startEPG: hasPush:" + hsPush);
        if (hsPush) {
            Intent intent = new Intent(mcontext, NotifyActivity.class);
            intent.putExtra("path", notifyBean.getJson().getPush_url());
            startActivity(intent);
            finish();
        } else {
            LogUtil.i(TAG, "startEPG: not Push");
            GroupStategyUtils.StartEPG(mcontext, deviceInfoBean);
        }
    }

    public static TextView closeTimeText = null;
    private long CloseTime = 0;

    public void removeTextView() {
        LogUtil.i(TAG, "removeTextView: ");
        if (closeTimeText != null) {
            main_frame.removeView(closeTimeText);
            closeTimeText = null;
        }
    }

    //是否从起始位置开始播放
    public static boolean isPlayFromStart = false;
    public static boolean isPlayPause = false;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceCreated window surfaceCreated....");
        surfaceView.setFocusable(false);// 设置键盘焦点
        surfaceView.setFocusableInTouchMode(false);// 设置触摸屏焦点
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LogUtil.i(TAG, "surfaceChanged");
    }

    public int currentP = 0;

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LogUtil.i(TAG, "surfaceDestroyed: ");
        close();
    }

    public void close() {
        LogUtil.i(TAG, "close: ");
        if (null != mediaPlayer) {
            currentP = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlayPause = true;
        }
    }

    public void play() {
        if (null != mediaPlayer) {
            try{
                // 设置显示到屏幕
                mediaPlayer.setDisplay(surfaceView.getHolder());
                mediaPlayer.start();
            }catch (IllegalArgumentException e1){
                e1.printStackTrace();
            } catch (IllegalStateException e1) {
                e1.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.i(TAG, "onCompletion: ");
//        if (null != mediaPlayer) {
//            mediaPlayer.start();
//        }
        if (null!=mp){
            mp.getDuration();
        }
        initCloseTime(0);
        if (TextUtils.isEmpty(downLoadVideo)){
            AdvertManage.uploadAdverLog(deviceInfoBean,mcontext,videoUrl);
        }else{
            AdvertManage.uploadAdverLog(deviceInfoBean,mcontext,videoUrl);
        }
    }

/**
 * MEDIA_ERROR_IO
 文件不存在或错误，或网络不可访问错误
 值: -1004 (0xfffffc14)
 MEDIA_ERROR_MALFORMED
 流不符合有关标准或文件的编码规范
 值: -1007 (0xfffffc11)
 MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK
 视频流及其容器不适用于连续播放视频的指标（例如：MOOV原子）不在文件的开始.
 值: 200 (0x000000c8)
 MEDIA_ERROR_SERVER_DIED
 媒体服务器挂掉了。此时，程序必须释放MediaPlayer 对象，并重新new 一个新的。
 值: 100 (0x00000064)
 MEDIA_ERROR_TIMED_OUT
 一些操作使用了过长的时间，也就是超时了，通常是超过了3-5秒
 值: -110 (0xffffff92)
 MEDIA_ERROR_UNKNOWN
 未知错误
 值: 1 (0x00000001)
 MEDIA_ERROR_UNSUPPORTED
 比特流符合相关编码标准或文件的规格，但媒体框架不支持此功能
 值: -1010 (0xfffffc0e)
 what int: 标记的错误类型:
 MEDIA_ERROR_UNKNOWN
 MEDIA_ERROR_SERVER_DIED
 extra int: 标记的错误类型.
 MEDIA_ERROR_IO
 MEDIA_ERROR_MALFORMED
 MEDIA_ERROR_UNSUPPORTED
 MEDIA_ERROR_TIMED_OUT
 MEDIA_ERROR_SYSTEM (-2147483648) - low-level system error.
 * */
    private boolean isFirstError = true;
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        LogUtil.i(TAG, "MediaPlayer onError: "+what);
        switch (what){
            case MediaPlayer.MEDIA_ERROR_IO:
                LogUtil.e(TAG,"文件不存在或错误，或网络不可访问错误");
                break;
            case MediaPlayer.MEDIA_ERROR_MALFORMED:
                LogUtil.e(TAG,"流不符合有关标准或文件的编码规范");
                break;
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                LogUtil.e(TAG,"视频流及其容器不适用于连续播放视频的指标（例如：MOOV原子）不在文件的开始.");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                LogUtil.e(TAG,"媒体服务器挂掉了");
                break;
            case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                LogUtil.e(TAG,"超时");
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                LogUtil.e(TAG,"未知错误");
                break;
            case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                LogUtil.e(TAG,"比特流符合相关编码标准或文件的规格，但媒体框架不支持此功能");
                break;
            default:
                LogUtil.e(TAG,"未知的异常:"+what);
                break;
        }
        if (null != mediaPlayer) {
            mediaPlayer.reset();
        }
        currentP = 0;
        if (isFirstError){
            isFirstError = false;
            setVideo(videoUrl);
        }
        return false;


    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtil.i(TAG, "onPrepared isPlayPause:" + isPlayPause + "|" + currentP);
        try{
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.start();
            // 播放视频
            if (isPlayFromStart) {
                mediaPlayer.seekTo(0);
                //马上置为false，因为视频开始播放以后，按home键切出去，再进来的话要从上次播放位置开始
                isPlayFromStart = false;
            } else {
                if (isPlayPause) {
                    if (currentP >= mediaPlayer.getDuration()) {
                        currentP = 0;
                    }
                    mediaPlayer.seekTo(currentP);
                } else {
                    mediaPlayer.seekTo(0);
                }
            }
        }catch (IllegalArgumentException e1){
            e1.printStackTrace();
        }catch (IllegalStateException e1) {
            e1.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "MainActivity onDestroy: ");
        if (surfaceView != null) {
            close();
        }
        System.gc();
        cancelCountDownTimer();
    }

    /**
     * 继续播放：在页面的onResume方法中使用
     */
    private boolean startPlay = true;

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i(TAG, "MainActivity onResume: ");
        if (startPlay) {
            startPlay = false;
            return;
        }
        if (!TextUtils.isEmpty(videoUrl)){
            setNextVideo(videoUrl);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(null != mediaPlayer){
            int  currentPosition = mediaPlayer.getCurrentPosition();
            LogUtil.i(TAG, "MainActivity onPause:" + currentPosition );
        }
    }

}
