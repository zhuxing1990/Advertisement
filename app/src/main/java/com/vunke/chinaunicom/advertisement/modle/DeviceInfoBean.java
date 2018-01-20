package com.vunke.chinaunicom.advertisement.modle;

/**
 * Created by zhuxi on 2018/1/4.
 */
public class DeviceInfoBean {

    private String username;
    private String password;
    private String user_token;
    private String auth_server;
    private String loginstatus;
    private String stb_id;

    private String EPGDomain;
    private String EPGGroupNMB;
    private String Area_id;
    private String Group_id;


    private String tokentime;
    private String NetAccount;
    private String NetPassword;
    private String authResultCode;
    private String authResultDesc;
    private String bindPhone;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_token() {
        return user_token;
    }

    public void setUser_token(String user_token) {
        this.user_token = user_token;
    }

    public String getAuth_server() {
        return auth_server;
    }

    public void setAuth_server(String auth_server) {
        this.auth_server = auth_server;
    }

    public String getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(String loginstatus) {
        this.loginstatus = loginstatus;
    }

    public String getStb_id() {
        return stb_id;
    }

    public void setStb_id(String stb_id) {
        this.stb_id = stb_id;
    }

    public String getEPGDomain() {
        return EPGDomain;
    }

    public void setEPGDomain(String EPGDomain) {
        this.EPGDomain = EPGDomain;
    }

    public String getEPGGroupNMB() {
        return EPGGroupNMB;
    }

    public void setEPGGroupNMB(String EPGGroupNMB) {
        this.EPGGroupNMB = EPGGroupNMB;
    }

    public String getArea_id() {
        return Area_id;
    }

    public void setArea_id(String area_id) {
        Area_id = area_id;
    }

    public String getGroup_id() {
        return Group_id;
    }

    public void setGroup_id(String group_id) {
        Group_id = group_id;
    }


    public String getTokentime() {
        return tokentime;
    }

    public void setTokentime(String tokentime) {
        this.tokentime = tokentime;
    }

    public String getNetAccount() {
        return NetAccount;
    }

    public void setNetAccount(String netAccount) {
        NetAccount = netAccount;
    }

    public String getNetPassword() {
        return NetPassword;
    }

    public void setNetPassword(String netPassword) {
        NetPassword = netPassword;
    }

    public String getAuthResultCode() {
        return authResultCode;
    }

    public void setAuthResultCode(String authResultCode) {
        this.authResultCode = authResultCode;
    }

    public String getAuthResultDesc() {
        return authResultDesc;
    }

    public void setAuthResultDesc(String authResultDesc) {
        this.authResultDesc = authResultDesc;
    }

    public String getBindPhone() {
        return bindPhone;
    }

    public void setBindPhone(String bindPhone) {
        this.bindPhone = bindPhone;
    }

    @Override
    public String toString() {
        return "DeviceInfoBean{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", user_token='" + user_token + '\'' +
                ", auth_server='" + auth_server + '\'' +
                ", loginstatus='" + loginstatus + '\'' +
                ", stb_id='" + stb_id + '\'' +
                ", EPGDomain='" + EPGDomain + '\'' +
                ", EPGGroupNMB='" + EPGGroupNMB + '\'' +
                ", Area_id='" + Area_id + '\'' +
                ", Group_id='" + Group_id + '\'' +
                '}';
    }
}
