package com.vunke.chinaunicom.advertisement.manager;

import com.vunke.chinaunicom.advertisement.modle.PlayVideoBean;

/**
 * Created by zhuxi on 2018/2/2.
 */
public class VideoTest {
    public static PlayVideoBean getTestVideo(){
        String url1 = "http://v.cctv.com/flash/mp4video6/TMS/2011/01/05/cf752b1c12ce452b3040cab2f90bc265_h264818000nero_aac32-1.mp4";
        String url2 = "http://img.58yicun.com/2/video/20170511/356e75f0e46e2642f99a50578cbc2758.mp4";
        PlayVideoBean playVideoBean = new PlayVideoBean();
        playVideoBean.setPlay_url(url1);
        playVideoBean.setMarginLeft(300);
        playVideoBean.setMarginTop(200);
        playVideoBean.setHeight(300);
        playVideoBean.setWidth(300);
        return playVideoBean;
    }
    public static PlayVideoBean getTestVideo2(){
        String url2 = "http://img.58yicun.com/2/video/20170511/356e75f0e46e2642f99a50578cbc2758.mp4";
        PlayVideoBean playVideoBean = new PlayVideoBean();
        playVideoBean.setPlay_url(url2);
        playVideoBean.setMarginLeft(400);
        playVideoBean.setMarginTop(200);
        playVideoBean.setHeight(400);
        playVideoBean.setWidth(500);
        return playVideoBean;
    }
}
