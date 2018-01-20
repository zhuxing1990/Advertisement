package com.vunke.chinaunicom.advertisement.modle;

/**
 * Created by zhuxi on 2018/1/13.
 */
public class GroupInfoBean {


    /**
     * code : 200
     * message : success
     * json : {"ApkPath":"","Area_id":"它传什么，你返回什么","EPGDomain":"它传什么，你返回什么","EPGcode":"","EPGpackage":"com.hunantv.operator","Group_id":"它传什么，你返回什么"}
     */

    private String code;
    private String message;
    /**
     * ApkPath :
     * Area_id : 它传什么，你返回什么
     * EPGDomain : 它传什么，你返回什么
     * EPGcode :
     * EPGpackage : com.hunantv.operator
     * Group_id : 它传什么，你返回什么
     */

    private JsonBean json;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JsonBean getJson() {
        return json;
    }

    public void setJson(JsonBean json) {
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

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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
