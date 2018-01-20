package com.vunke.chinaunicom.advertisement.modle;

/**
 * Created by zhuxi on 2017/6/14.
 */
public class PlayVideoBean {

    /**
     * play_url : http://www.baidu.com
     * is_loop : false
     * width : 815
     * height : 455
     * marginLeft : 450
     * marginTop : 10
     * marginRight : 10
     * marginBottom : 0
     * paddingLeft : 10
     * paddingTop : 0
     * paddingRight : 10
     * paddingBottom : 450
     * reserve :
     * time : 500
     * close_time : 999
     */

    private String play_url;
    private boolean is_loop;
    private int width;
    private int height;
    private int marginLeft;
    private int marginTop;
    private int marginRight;
    private int marginBottom;
    private int paddingLeft;
    private int paddingTop;
    private int paddingRight;
    private int paddingBottom;
    private String reserve;
    private int time;
    private int close_time;

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public boolean isIs_loop() {
        return is_loop;
    }

    public void setIs_loop(boolean is_loop) {
        this.is_loop = is_loop;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getClose_time() {
        return close_time;
    }

    public void setClose_time(int close_time) {
        this.close_time = close_time;
    }
}
