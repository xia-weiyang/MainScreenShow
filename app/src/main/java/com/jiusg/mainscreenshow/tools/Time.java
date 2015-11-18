package com.jiusg.mainscreenshow.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class Time {

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMDHMSTime(){
		
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd HH:mm:ss
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowMDHMSTime(){
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"MM-dd HH:mm:ss");
		String date = mDateFormat.format(new Date());
		return date;
	}
	/**
	 * MM-dd
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getNowYMD(){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String date = mDateFormat.format(new Date());
		return date;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getYMD(Date date){

		SimpleDateFormat mDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String dateS = mDateFormat.format(date);
		return dateS;
	}
}
