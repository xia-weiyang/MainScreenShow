package com.jiusg.mainscreenshow.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.annotation.SuppressLint;
import android.util.Log;

/**
 * 带日志文件输入的，又可控开关的日志调试
 * 
 * @author BaoHang
 * @version 1.0
 * @data 2012-2-20
 */
@SuppressLint("SimpleDateFormat")
public class MyLog {
	public static Boolean MYLOG_SWITCH = false; // 日志文件总开关
	private static Boolean MYLOG_WRITE_TO_FILE = true;// 日志写入文件开关
	private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
	@SuppressLint("SdCardPath")
	private static String MYLOG_PATH_SDCARD_DIR1 = "/sdcard/MSShow";    // 日志文件在sdcard中的路径
	private static String MYLOG_PATH_SDCARD_DIR2 = "/Log";
	private static String MYLOG_PATH_SDCARD_DIR = MYLOG_PATH_SDCARD_DIR1+MYLOG_PATH_SDCARD_DIR2;
	private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 日志的输出格式

	public static void w(String tag, Object msg) { // 警告信息
		log(tag, msg.toString(), 'w');
	}

	public static void e(String tag, Object msg) { // 错误信息
		log(tag, msg.toString(), 'e');
	}

	public static void d(String tag, Object msg) {// 调试信息
		log(tag, msg.toString(), 'd');
	}

	public static void i(String tag, Object msg) {//
		log(tag, msg.toString(), 'i');
	}

	public static void v(String tag, Object msg) {
		log(tag, msg.toString(), 'v');
	}

	public static void w(String tag, String text) {
		log(tag, text, 'w');
	}

	public static void e(String tag, String text) {
		log(tag, text, 'e');
	}

	public static void d(String tag, String text) {
		log(tag, text, 'd');
	}

	public static void i(String tag, String text) {
		log(tag, text, 'i');
	}

	public static void v(String tag, String text) {
		log(tag, text, 'v');
	}

	/**
	 * 根据tag, msg和等级，输出日志
	 * 
	 * @param tag
	 * @param msg
	 * @param level
	 * @return void
	 * @since v 1.0
	 */
	private static void log(String tag, String msg, char level) {
		msg = "\r\n"+msg;
		if (MYLOG_SWITCH) {
			if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
				Log.e(tag, msg);
			} else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.w(tag, msg);
			} else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.d(tag, msg);
			} else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
				Log.i(tag, msg);
			} else {
				Log.v(tag, msg);
			}
			if (MYLOG_WRITE_TO_FILE)
				writeLogtoFile(String.valueOf(level), tag, msg);
		}
	}

	/**
	 * 打开日志文件并写入日志
	 * 
	 * @return
	 * **/
	private static void writeLogtoFile(String mylogtype, String tag, String text) {// 新建或打开日志文件
		isExist(MYLOG_PATH_SDCARD_DIR1);
		isExist(MYLOG_PATH_SDCARD_DIR);
		Date nowtime = new Date();
//		String needWriteFiel = logfile.format(nowtime);
		String needWriteMessage = myLogSdf.format(nowtime) + "    " + mylogtype
				+ "    " + tag + "    " + text;
		File file = new File(MYLOG_PATH_SDCARD_DIR, MYLOGFILEName);
		try {
			FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 删除制定的日志文件
	 * */
	public static void delFile() {// 删除日志文件

		File file = new File(MYLOG_PATH_SDCARD_DIR, MYLOGFILEName);
		if (file.exists()) {
			file.delete();
		}
	}

	public static void isExist(String path) {
		File file = new File(path);
		// 判断文件夹是否存在,如果不存在则创建文件夹
		if (!file.exists()) {
			file.mkdir();
		}

	}
}