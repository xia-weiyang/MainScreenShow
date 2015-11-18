package com.jiusg.mainscreenshow.tools;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class Download {
	/** 连接url */
	private String urlstr;
	/** http连接管理类 */
	private HttpURLConnection urlcon;

	private int downSize = 0;

	public Download(String url) {
		this.urlstr = url;
		urlcon = getConnection();
	}

	/*
	 * 读取网络文本
	 */
	public String downloadAsString() {
		StringBuilder sb = new StringBuilder();
		String temp = null;
		try {
			InputStream is = urlcon.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	/*
	 * 获取http连接处理类HttpURLConnection
	 */
	private HttpURLConnection getConnection() {
		URL url;
		HttpURLConnection urlcon = null;
		try {
			url = new URL(urlstr);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setDoInput(true);
			urlcon.setConnectTimeout(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return urlcon;
	}

	/*
	 * 获取连接文件长度。
	 */
	public int getLength() {

		return urlcon.getContentLength();
	}

	public void getLenghtInBackground(final DoneSizeCallback sizeCallback,
			final FailedCallback failedCallback) {

		new Thread() {
			public void run() {

				try {
					int size = getLength();
					sizeCallback.Done(size);
				} catch (Exception e) {
					failedCallback.failed(e);
				}

			};
		}.start();
	}

	public byte[] downBytes() throws IOException {
		InputStream inputStream = urlcon.getInputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();

	}

	public int dowmSdInBackground(final String dir, final String filename,
			final DoneCallBack doneCallBack,
			final ProgressCallback progressCallback,
			final FailedCallback failedCallback) {
		new Thread() {

			@Override
			public void run() {
				down2sd(dir, filename, doneCallBack, progressCallback,
						failedCallback);
				super.run();
			}

		}.start();
		return 1;
	}

	/*
	 * 写文件到sd卡 demo 前提需要设置模拟器sd卡容量，否则会引发EACCES异常 先创建文件夹，在创建文件
	 */
	public int down2sd(String dir, String filename, DoneCallBack doneCallBack,
			ProgressCallback progressCallback, FailedCallback failedCallback) {

		StringBuilder sb = new StringBuilder(dir);

		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
			// 创建文件夹
			Log.d("Dowloag", sb.toString());
		}
		// 获取文件全名
		sb.append(filename);
		file = new File(sb.toString());

		FileOutputStream fos = null;
		try {
			InputStream is = urlcon.getInputStream();

			// 创建文件
			file.createNewFile();
			fos = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = is.read(buf)) != -1) {
				fos.write(buf, 0, len);

				progressCallback.done(countProgress(len));
			}

			is.close();
			doneCallBack.done(sb.toString());
		} catch (Exception e) {
			if (failedCallback != null)
				failedCallback.failed(e);
			e.printStackTrace();
			return 0;
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				if (failedCallback != null)
					failedCallback.failed(e);
				e.printStackTrace();
			}
		}
		return 1;
	}

	private int countProgress(int len) {

		downSize += len;
		float ds = downSize;
		float size = getLength();

		return (int) (ds / size * 100);
	}

	/*
	 * 内部回调接口类
	 */

	public abstract class DoneCallBack {
		public abstract void done(String path);
	}

	public abstract class ProgressCallback {
		public abstract void done(int progress);
	}

	public abstract class FailedCallback {
		public abstract void failed(Exception e);
	}

	public abstract class DoneSizeCallback {
		public abstract void Done(int size);
	}
}
