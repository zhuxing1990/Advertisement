package com.vunke.chinaunicom.advertisement.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.vunke.chinaunicom.advertisement.activity.PictureActivity;
import com.vunke.chinaunicom.advertisement.log.LogUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuxi on 2017/12/26.
 */
public class Utils {
    private static final String TAG = "Utils";
    public static boolean isNetworkConnected(Context ct) {
        ConnectivityManager cManager = (ConnectivityManager) ct
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cManager != null) {
            NetworkInfo localNetworkInfo = cManager.getActiveNetworkInfo();
            if (localNetworkInfo != null)
                return localNetworkInfo.isConnected();
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    /**
     * 判断当前网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info!=null) {
                    boolean istrue = false;
                    istrue= istrue?info.isConnected():info.isAvailable();
                    return istrue;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    /**
     * 吐司
     */
    public static void ShowToast(Context mcontext, CharSequence str) {
        if (mcontext == null) {
            LogUtil.i(TAG, "ShowToast()无法获取上下文");
            return;
        }
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(mcontext, "未定义提示内容", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(mcontext, str, Toast.LENGTH_SHORT).show();
    }


    public static void sendBroadCast(Context mcontext, String Action,
                                     Intent intent) {
        intent.setAction(Action);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        mcontext.sendBroadcast(intent);
    }
    /**
     * 芒果 内部 获取 10段开头的IP地址
     *
     * @return
     */
    public static String getIpAddr() {

        String str = getIpNetcfg("ppp[0-9]+");
        if ((TextUtils.isEmpty(str)) || ("0.0.0.0".equals(str))) {
            str = getIpNetcfg("eth[0-9]+");
        }
        return str;
    }

    public static String getIpNetcfg(String paramString) {
        try {
            Process process = Runtime.getRuntime().exec("netcfg");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            process.waitFor();
            Pattern pattern = Pattern.compile("^([a-z0-9]+)\\s+(UP|DOWN)\\s+([0-9./]+)\\s+.+\\s+([0-9a-f:]+)$", Pattern.CASE_INSENSITIVE);
            String str5;
            Matcher matcher = null;
            String ready ;
            while ((ready = bufferedReader.readLine()) != null) {
//                LogUtil.i(TAG, "getIpNetcfg: ready:"+ready);
                matcher = pattern.matcher(ready);
                if (matcher!=null&&matcher.matches()){
                    String str1 = matcher.group(1).toLowerCase(Locale.CHINA);
//                    String str2 = matcher.group(2);
                    String str3 = matcher.group(3);
//                    String str4 = matcher.group(4).toUpperCase(Locale.CHINA).replace(':', '-');
//                    LogUtil.i("", "match success:" + str1 + " " + str2 + " " + str3 + " " + str4);
                    LogUtil.i(TAG, "match success:str1:"+str1+"\t str3:" + str3);
                    if(str1.matches(paramString)){
                        str5 = str3.substring(0, str3.indexOf("/"));
                        LogUtil.i(TAG, "addr:" + str5);
                        return str5;
                    }
                }
            }
        } catch (java.io.IOException IOException) {
            LogUtil.i(TAG, "Exception: IOException.");
            IOException.printStackTrace();
        } catch (InterruptedException InterruptedException) {
            LogUtil.i(TAG, "Exception: InterruptedException.");
            InterruptedException.printStackTrace();
        }
        return "";
    }
    /**
     * @param context
     * @return versionName 版本名字
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
        return versionName;
    }

    /**
     * @param context
     * @return versionCode 版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return versionCode;
    }

    /**
     * 判断应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isPkgInstalled(Context context, String packageName) {
        LogUtil.i(TAG, "isPkgInstalled: getPackageName:"+packageName);
        if (TextUtils.isEmpty(packageName)) {
            LogUtil.i(TAG, "isPkgInstalled: get packageName is null");
            return false;
        }
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }


    /**
     * 根据包名启动APK
     *
     * @param packageName
     * @param context
     */
    public static void StartEPG(String packageName, Context context) {
        if (TextUtils.isEmpty(packageName)) {
            LogUtil.i(TAG, "包名为空");
            return;
        }
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);
            PackageManager pManager = context.getPackageManager();
            List apps = pManager.queryIntentActivities(resolveIntent, 0);
            ResolveInfo ri = (ResolveInfo) apps.iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cn = new ComponentName(packageName, className);
                intent.setComponent(cn);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void startPictureActivity(Activity activity) {
        Intent intent = new Intent(activity, PictureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 根据包名启动服务
     *
     * @param packageName
     * @param context
     */
    public static void StartServer(String packageName,String className,String Action,Context context) {
        if (TextUtils.isEmpty(packageName)) {
            LogUtil.i(TAG, "包名为空");
            return;
        }
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
            if (pi!=null){
                LogUtil.i(TAG,"StartServer packageName:"+packageName);
                LogUtil.i(TAG,"StartServer className:"+className);
                LogUtil.i(TAG,"StartServer Action:"+Action);
//				Intent intent = new Intent(Intent.ACTION_MAIN);
//				ComponentName cn = new ComponentName(packageName, className);
//				intent.setAction(Action);
//				context.startService(intent);
                Intent intent = new Intent(className);
                intent.setPackage(packageName);
                if (!TextUtils.isEmpty(Action)){
                    intent.setAction(Action);
                }
                context.startService(intent);
                LogUtil.i(TAG,"StartServer to AuthApk,start time:"+new Date());
                SharedPreferencesUtil.setBooleanValue(context,"IPTV_Service", true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 判断服务是否运行
     *
     * @param context
     * @param clazz
     *            要判断的服务的class
     * @return
     */
    public static boolean isServiceRunning(Context context,
                                           Class<? extends Service> clazz) {
        try {
            ActivityManager manager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);

            List<ActivityManager.RunningServiceInfo> services = manager.getRunningServices(100);
            for (int i = 0; i < services.size(); i++) {
                String className = services.get(i).service.getClassName();
                if (className.equals(clazz.getName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
