package com.vunke.chinaunicom.advertisement.log;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志工具类
 * @author zhuxi
 *
 */
public class LogUtil {
	
	private static boolean DEBUG_MODE = true;
	private static final String LogName = "Advertisement";

	public static void i(String className, String content) {
		Log.i(className, "time:" + getDateTime() + ";" + "[i]" + ";"
				+ LogName+"\t" + content);
	}
	public static void a(String content) {
		Log.d("System.out", "time:" + getDateTime() + ";" + "[e]" + ";"
				+ LogName+"\t" + content);
	}

	public static void d(String className, String content) {
		Log.d(className, "time:" + getDateTime() + ";" + "[d]" + ";"
				+ LogName+"\t" + content);
	}

	public static void e(String className, String content) {
		Log.e(className, "time:" + getDateTime() + ";" + "[e]" + ";"
				+ LogName+"\t" + content);
	}

	public static void w(String className, String content) {
		Log.w(className, "time:" + getDateTime() + ";" + "[w]" + ";"
				+ LogName+"\t" + content);
	}

	public static void v(String className, String content) {
		Log.v(className, "time:" + getDateTime() + ";" + "[v]" + ";"
				+ LogName+"\t" + content);
	}
	
	public static void i(String className, String content, Throwable e) {
		Log.i(className, "time:" + getDateTime() + ";" + "[i]" + ";"
				+ LogName+"\t" + content, e);
	}
	public static void a(String content, Throwable e) {
		Log.d("System.out", "time:" + getDateTime() + ";" + "[e]" + ";"
				+ LogName+"\t" + content,e);
	}
	public static void d(String className, String content, Throwable e) {
		Log.d(className, "time:" + getDateTime() + ";" + "[d]" + ";"
				+ LogName+"\t" + content, e);
	}

	public static void e(String className, String content, Throwable e) {
		Log.e(className, "time:" + getDateTime() + ";" + "[e]" + ";"
				+ LogName+"\t" + content, e);
	}

	public static void w(String className, String content, Throwable e) {
		Log.w(className, "time:" + getDateTime() + ";" + "[w]" + ";"
				+ LogName+"\t" + content, e);
	}

	public static void v(String className, String content, Throwable e) {
		Log.v(className, "time:" + getDateTime() + ";" + "[v]" + ";"
				+ LogName+"\t" + content, e);
	}

	/*
	 * public static void main(String[] args) { WorkLog.a("class:" +
	 * "worleLog"); WorkLog.a("time:" + getDateTime() + ";" + "[d]" +
	 * ";"); WorkLog.a("content:"+"当前内容"); }
	 */

	/**
	 * 获取系统时间
	 * 
	 * @return String 2016-6-12 10:53:05:888
	 */
	public static String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss:SS");
		Date date = new Date(System.currentTimeMillis());
		String time = dateFormat.format(date);
		return time;
	}
	
}