package com.vunke.chinaunicom.advertisement.modle;

import java.util.List;

/**
 * Created by zhuxi on 2018/1/10.
 */
public class GroupStrategyBean {

    /**
     * ApkPath : http://119.39.118.164:8082/AppStoreJoTV/apk/stbauth/stbauthApp_20180109_03_versionCode[2].apk
     * Area_id :
     * EPGDomain : ZTE
     * EPGcode :
     * EPGpackage : com.hunantv.operator
     * Group_id : 0
     */

    private List<JsonBean> json;

    public List<JsonBean> getJson() {
        return json;
    }

    public void setJson(List<JsonBean> json) {
        this.json = json;
    }

    public static class JsonBean {
        private String ApkPath;
        private String Area_id;
        private String EPGDomain;
        private String EPGcode;
        private String EPGpackage;
        private String Group_id;
        private String userName;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getApkPath() {
            return ApkPath;
        }

        public void setApkPath(String ApkPath) {
            this.ApkPath = ApkPath;
        }

        public String getArea_id() {
            return Area_id;
        }

        public void setArea_id(String Area_id) {
            this.Area_id = Area_id;
        }

        public String getEPGDomain() {
            return EPGDomain;
        }

        public void setEPGDomain(String EPGDomain) {
            this.EPGDomain = EPGDomain;
        }

        public String getEPGcode() {
            return EPGcode;
        }

        public void setEPGcode(String EPGcode) {
            this.EPGcode = EPGcode;
        }

        public String getEPGpackage() {
            return EPGpackage;
        }

        public void setEPGpackage(String EPGpackage) {
            this.EPGpackage = EPGpackage;
        }

        public String getGroup_id() {
            return Group_id;
        }

        public void setGroup_id(String Group_id) {
            this.Group_id = Group_id;
        }

        @Override
        public String toString() {
            return "JsonBean{" +
                    "ApkPath='" + ApkPath + '\'' +
                    ", Area_id='" + Area_id + '\'' +
                    ", EPGDomain='" + EPGDomain + '\'' +
                    ", EPGcode='" + EPGcode + '\'' +
                    ", EPGpackage='" + EPGpackage + '\'' +
                    ", Group_id='" + Group_id + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }
    }
}
