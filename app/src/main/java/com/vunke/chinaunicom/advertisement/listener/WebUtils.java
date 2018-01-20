package com.vunke.chinaunicom.advertisement.listener;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.vunke.chinaunicom.advertisement.log.LogUtil;

/**
 * Created by zhuxi on 2018/1/9.
 */
public class WebUtils {
    private static final String TAG = "WebUtils";

    public static void SetWebView(final WebView webview,Context context){
        WebSettings settings = webview.getSettings();
        // 支持js
        settings.setJavaScriptEnabled(true);
        // 设置字符编码
        settings.setDefaultTextEncodingName("GBK");
//        settings.setDefaultTextEncodingName("UTF-8");// 设置字符编码
        // 启用支持javascript
        settings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        settings.setBuiltInZoomControls(true);
        settings.setLightTouchEnabled(true);
        settings.setSupportZoom(true);
//        settings.setUseWideViewPort(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 不使用缓存，只从网络获取数据.
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // settings.setLoadWithOverviewMode(true);
        // 支持JS交互
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webview.setDownloadListener(new MyWebViewDownLoadListener(context));
    }
}
