package com.vunke.chinaunicom.advertisement.manager;

/**
 * Created by zhuxi on 2017/12/26.
 */
public class URL_Manager {
    public static final String BASE_URL = "http://119.39.118.163:8082/";//正式地址
//    public static final String BASE_URL = "http://118.39.118.163:8082/";//测试地址
//    public static final String BASE_URL = "http://119.39.118.162:8082/";//测试地址



//public static final String BASE_URL2 = "http://119.39.118.164:8082/";//测试地址
//    public static final String ADVERTISEMENT_IPTVSTRATEGY_URL = "IptvProject/IptvStrategyService.do";//测试获取更新策略接口

    public static final String ADVERTISEMENT_IPTVSTRATEGY_URL = "Advertise/IptvStrategyService.do";//正式获取更新策略接口

//    public static final String ADVERTISEMENT_PUSH_URL = "IptvProject/adpush_server/push.do";//测试
    public static final String ADVERTISEMENT_PUSH_URL = "Advertise/adpush_server/push.do";//正式


//    public static final String ADVERTISEMENT_GROUPSTRATEGY = "IptvProject/GroupStrategy.do";//测试

    public static final String ADVERTISEMENT_GROUPSTRATEGY = "Advertise/GroupStrategy.do";//正式

    public static final String ADVERTISEMENT_UPLOADLOG_URL = "Advertise/adpush_server/uploadLog.do";//正式
//    public static final String ADVERTISEMENT_GROUPSTRATEGY2 = "GroupStrategy/GroupStrategy.txt";//测试
}
