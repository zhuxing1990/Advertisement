package com.vunke.chinaunicom.advertisement.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.vunke.chinaunicom.advertisement.log.LogUtil;
import com.vunke.chinaunicom.advertisement.modle.DeviceInfoBean;
import com.vunke.chinaunicom.advertisement.modle.GroupInfoBean;

/**
 * Created by zhuxi on 2018/1/10.
 */
public class GroupStategyUtils {
    private static final String TAG = "GroupStategyUtils";

    public static void InsertGroupStrategy(Context context, GroupInfoBean.JsonBean bean) {
        LogUtil.i(TAG, "InsertGroupStrategy: ");
        Uri uri = Uri
                .parse("content://com.vunke.chinaunicom.advertisement.groupstrategy/group_strategy");
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        try {
            values.put("epg_code", bean.getEPGcode());
            values.put("epg_package", bean.getEPGpackage());
            values.put("create_time", System.currentTimeMillis());
            values.put("userName", bean.getUserName());
            values.put("apk_path", bean.getApkPath());
            values.put("area_id",bean.getArea_id());
            values.put("epg_domain",bean.getEPGDomain());
            values.put("group_id",bean.getGroup_id());
            Uri uri2 = resolver.insert(uri, values); // 内部调用内容提供者的insert方法
            LogUtil.i(TAG, "insert date to group_strategy" );
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 查询数据库的代码
        // Cursor query = resolver.query(uri, null, null, null, null);
        // while (query.moveToNext()) {
        // // System.err.println(query.getString(query.getColumnIndex("body")));
        // // System.err
        // // .println(query.getString(query.getColumnIndex("user_id")));
        // }
    }
    @NonNull
    public static GroupInfoBean.JsonBean getGroupStrategyBean(Context context, String userName) {
        LogUtil.i(TAG, "getGroupStrategyBean: ");
        String[] strings = new String[]{userName.trim()};
        Uri localUri = Uri
                .parse("content://com.vunke.chinaunicom.advertisement.groupstrategy/group_strategy");
        Cursor localCursor = context.getContentResolver().query(localUri,
                null, null, strings, null);
        GroupInfoBean.JsonBean bean = new GroupInfoBean.JsonBean();
        try {
            if (localCursor!=null){
                if (localCursor.moveToNext()) {
                    bean.setEPGcode(localCursor.getString(localCursor
                            .getColumnIndex("epg_code")));
                    bean.setEPGpackage(localCursor.getString(localCursor
                            .getColumnIndex("epg_package")));
                    bean.setGroup_id(localCursor.getString(localCursor
                            .getColumnIndex("group_id")));
                    bean.setEPGDomain(localCursor.getString(localCursor
                            .getColumnIndex("epg_domain")));
                    bean.setArea_id(localCursor.getString(localCursor
                            .getColumnIndex("area_id")));
                    bean.setApkPath(localCursor.getString(localCursor
                            .getColumnIndex("apk_path")));
                    bean.setUserName(localCursor.getString(localCursor
                            .getColumnIndex("userName")));
                    // bean.setCreateTime(localCursor.getString(localCursor.getColumnIndex("create_time")));
                }
            }else{
                LogUtil.e(TAG, "get group_strategy failed ,sql select is null");
                bean.setUserName(userName.trim());
                GroupStategyUtils.StartLastEpg(context, bean);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "get group_strategy error ,sql select failed");
            bean.setUserName(userName.trim());
            GroupStategyUtils.StartLastEpg(context, bean);
        } finally {
            if (localCursor != null)
                localCursor.close();
        }
        return bean;
    }
    public static void StartLastEpg(Context context,  GroupInfoBean.JsonBean bean) {
        LogUtil.e(TAG,
                "get epg_package info failed ,start'up last time epg");
        if (TextUtils.isEmpty(bean.getUserName())){
            LogUtil.e(TAG,"get user_id is null,start'up mangguo eog");
            StartMangGuoEPG(context);
            return;
        }
        String packageName = getPackageName(context, bean.getUserName(),
                "com.hunantv.operator");
        if (packageName.equals("com.hunantv.operator")){
            LogUtil.i(TAG, "StartLastEpg: get epg_package is mgtv");
            StartMangGuoEPG(context);
            setPackageName(context, bean.getUserName(), packageName);
            return;
        }
        try {

            if (Utils.isPkgInstalled(context,packageName)) {
                LogUtil.e(TAG,
                        "get epg_package info success ,start last time epg :"
                                + packageName);
                Utils.StartEPG(packageName, context);
                setPackageName(context, bean.getUserName(), packageName);
            } else {
                LogUtil.e(TAG,
                        "get epg_package info failed ,start'up mangguo EPG ");
                StartMangGuoEPG(context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 获取上次启动信息
     *
     * @param context
     * @param key
     * @param defultValue
     * @return
     */
    public static String getPackageName(Context context, String key,
                                        String defultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        String result = "";
        if (null != sp) {
            result = sp.getString(key, defultValue);
        }
        return result;
    }
    /**
     * 设置本次启动信息
     *
     * @param context
     * @param key
     * @param vaule
     */
    public static void setPackageName(Context context, String key, String vaule) {
        SharedPreferences sp = context.getSharedPreferences(
                context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, vaule);
        edit.commit();
    }
    public static void StartMangGuoEPG(Context context) {
        LogUtil.i(TAG, "StartMangGuoEPG: ");
        SharedPreferencesUtil.setBooleanValue(context,
                SharedPreferencesUtil.IS_PALYED_ADVERT, true);
        try {
            if (Utils.isPkgInstalled(context,"com.hunantv.operator")){
                Utils.StartEPG("com.hunantv.operator",context);
            }else{
                Utils.ShowToast(context,"应用未安装,启动失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void StartEPG(Activity mcontext, DeviceInfoBean deviceInfoBean){
        try {
            if (deviceInfoBean!=null&&!TextUtils.isEmpty(deviceInfoBean.getUsername())) {
                GroupInfoBean.JsonBean groupStrategyBean = GroupStategyUtils.getGroupStrategyBean(mcontext, deviceInfoBean.getUsername());
                LogUtil.i(TAG, "startEPG: get groupstrategyBean:" + groupStrategyBean.toString());
                if (!TextUtils.isEmpty(groupStrategyBean.getEPGpackage())) {
                    if (Utils.isPkgInstalled(mcontext, groupStrategyBean.getEPGpackage())) {
                        LogUtil.e(TAG, "get epg_package info success ,start epg :");
                        Utils.StartEPG(groupStrategyBean.getEPGpackage(), mcontext);
                        SharedPreferencesUtil.setBooleanValue(mcontext,
                                SharedPreferencesUtil.IS_PALYED_ADVERT, true);
                        mcontext.finish();
                    } else {
                        LogUtil.e(TAG, "get epg_package info failed ,start last epg :");
                        if (!TextUtils.isEmpty(groupStrategyBean.getApkPath())) {
                            LogUtil.i(TAG, "not installed apk,start Download apk");
                            SilenceInstallUtils.DonloadEpgApk(mcontext, groupStrategyBean.getApkPath());
                        }
                        GroupStategyUtils.StartLastEpg(mcontext, groupStrategyBean);
                        SharedPreferencesUtil.setBooleanValue(mcontext,
                                SharedPreferencesUtil.IS_PALYED_ADVERT, true);
                        mcontext.finish();
                    }
                } else {
                    LogUtil.i(TAG, "startEPG: get epg_package is null");
                    GroupStategyUtils.StartLastEpg(mcontext, groupStrategyBean);
                    SharedPreferencesUtil.setBooleanValue(mcontext,
                            SharedPreferencesUtil.IS_PALYED_ADVERT, true);
                    mcontext.finish();
                }
            }else{
                GroupStategyUtils.StartMangGuoEPG(mcontext);
                SharedPreferencesUtil.setBooleanValue(mcontext,
                        SharedPreferencesUtil.IS_PALYED_ADVERT, true);
                mcontext.finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
