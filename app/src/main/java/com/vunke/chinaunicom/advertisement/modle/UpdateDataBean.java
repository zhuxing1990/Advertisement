package com.vunke.chinaunicom.advertisement.modle;

import java.util.List;

/**
 * Created by zhuxi on 2017/12/27.
 */
public class UpdateDataBean {


    /**
     * code : 200
     * json : {"updateTime":"20180108","templateTYPE":99,"templateUrl":"http://","imageDownloadUrl":["http://119.39.118.164:8082/image/IPTV+_1.jpg"],"videoDownloadUrl":"http://119.39.118.164:8082/video/base_video.mp4","videoPlayTime":0,"imagePlayTime":30}
     * message : success
     */

    private String code;
    /**
     * updateTime : 20180108
     * templateTYPE : 99
     * templateUrl : http://
     * imageDownloadUrl : ["http://119.39.118.164:8082/image/IPTV+_1.jpg"]
     * videoDownloadUrl : http://119.39.118.164:8082/video/base_video.mp4
     * videoPlayTime : 0
     * imagePlayTime : 30
     */

    private JsonBean json;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JsonBean getJson() {
        return json;
    }

    public void setJson(JsonBean json) {
        this.json = json;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class JsonBean {
        private String updateTime;
        private int templateTYPE;
        private String templateUrl;
        private String videoDownloadUrl;
        private int videoPlayTime;
        private int imagePlayTime;
        private List<String> imageDownloadUrl;

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public int getTemplateTYPE() {
            return templateTYPE;
        }

        public void setTemplateTYPE(int templateTYPE) {
            this.templateTYPE = templateTYPE;
        }

        public String getTemplateUrl() {
            return templateUrl;
        }

        public void setTemplateUrl(String templateUrl) {
            this.templateUrl = templateUrl;
        }

        public String getVideoDownloadUrl() {
            return videoDownloadUrl;
        }

        public void setVideoDownloadUrl(String videoDownloadUrl) {
            this.videoDownloadUrl = videoDownloadUrl;
        }

        public int getVideoPlayTime() {
            return videoPlayTime;
        }

        public void setVideoPlayTime(int videoPlayTime) {
            this.videoPlayTime = videoPlayTime;
        }

        public int getImagePlayTime() {
            return imagePlayTime;
        }

        public void setImagePlayTime(int imagePlayTime) {
            this.imagePlayTime = imagePlayTime;
        }

        public List<String> getImageDownloadUrl() {
            return imageDownloadUrl;
        }

        public void setImageDownloadUrl(List<String> imageDownloadUrl) {
            this.imageDownloadUrl = imageDownloadUrl;
        }
    }
}
