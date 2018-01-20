package com.vunke.chinaunicom.advertisement.modle;

/**
 * Created by zhuxi on 2018/1/9.
 */
public class NotifyBean {


    /**
     * code : 200
     * json : {"push_id":1,"push_type":99,"push_url":"http://119.39.118.164:8082/index.html","reserve":"预留信息","userName":"test"}
     * message : success
     */

    private String code;
    /**
     * push_id : 1
     * push_type : 99
     * push_url : http://119.39.118.164:8082/index.html
     * reserve : 预留信息
     * userName : test
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
        private int push_id;
        private int push_type;
        private String push_url;
        private String reserve;
        private String userName;

        public int getPush_id() {
            return push_id;
        }

        public void setPush_id(int push_id) {
            this.push_id = push_id;
        }

        public int getPush_type() {
            return push_type;
        }

        public void setPush_type(int push_type) {
            this.push_type = push_type;
        }

        public String getPush_url() {
            return push_url;
        }

        public void setPush_url(String push_url) {
            this.push_url = push_url;
        }

        public String getReserve() {
            return reserve;
        }

        public void setReserve(String reserve) {
            this.reserve = reserve;
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
                    "push_id=" + push_id +
                    ", push_type=" + push_type +
                    ", push_url='" + push_url + '\'' +
                    ", reserve='" + reserve + '\'' +
                    ", userName='" + userName + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NotifyBean{" +
                "code='" + code + '\'' +
                ", json=" + json +
                ", message='" + message + '\'' +
                '}';
    }
}
