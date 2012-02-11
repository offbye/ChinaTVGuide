package com.offbye.chinatvguide.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class HttpUtil {
	
	private static final int READ_TIMEOUT = 20000;
	private static final int CONNECT_TIMEOUT = 40000;
	private static final String TAG = "HttpUtil";

	public static boolean isCmwap(Context context) throws AppException {
		boolean isCmwap = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		//for ophone only
		int result = cwjManager.startUsingNetworkFeature(
                ConnectivityManager.TYPE_MOBILE, "internet");
		if(-1 == result)
		{
			result = cwjManager.startUsingNetworkFeature(
	                ConnectivityManager.TYPE_MOBILE, "wap");
		}
		if(-1 == result)
		{
			result = cwjManager.startUsingNetworkFeature(
	                ConnectivityManager.TYPE_MOBILE, "cmwap");
		}
		if(-1 == result)
		{
			result = cwjManager.startUsingNetworkFeature(
	                ConnectivityManager.TYPE_MOBILE, "cmnet");
		}
		//for ophone only end
		
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			Log.i(TAG," "+info.getTypeName());
			Log.i(TAG," "+info.getExtraInfo());
			if ("cmwap".equalsIgnoreCase(info.getExtraInfo())) {
				isCmwap = true;
			}
		}
		else
		{
			throw new AppException(500);
		}
		return isCmwap;
	}

	public static String getURLByCmwap(String weburl) throws IOException {
	    Log.d(TAG, weburl);
		StringBuilder sb = new StringBuilder(10000);
		URL url;
		HttpURLConnection conn;

		for (int i = 0; i < 5; i++) {

			url = new URL(weburl);
			Properties systemProperties = System.getProperties();
			systemProperties.setProperty("http.proxyHost", "10.0.0.172");
			systemProperties.setProperty("http.proxyPort", "80");

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-agent", "openwave");

			conn.connect();

			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line = "";
			Log.i(TAG, conn.getContentType());
			if (!conn.getContentType().startsWith("text/vnd.wap.wml")) {
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				conn.disconnect();
				break;
			} else {
				conn.disconnect();
			}
		}
		return sb.toString();
	}

	public static String getURL(String weburl) throws IOException {
		StringBuilder sb = new StringBuilder(10000);
		URL url;
		HttpURLConnection conn;
		url = new URL(weburl);
		conn = (HttpURLConnection) url.openConnection();

		conn.setConnectTimeout(CONNECT_TIMEOUT);
		conn.setReadTimeout(READ_TIMEOUT);
		// Get the response
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));
		String line = "";
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		conn.disconnect();

		return sb.toString();
	}

	public static String getUrl(Context context, String url) throws IOException, AppException {
		if (isCmwap(context)) {
			return getURLByCmwap(url);
		} else {
			return getURL(url);
		}
	}
	
	public static InputStream getXMLStreamByCmwap(String weburl) throws IOException {
		URL url;
		HttpURLConnection conn;

		for (int i = 0; i < 5; i++) {

			url = new URL(weburl);
			Properties systemProperties = System.getProperties();
			systemProperties.setProperty("http.proxyHost", "10.0.0.172");
			systemProperties.setProperty("http.proxyPort", "80");

			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-agent", "openwave");

			conn.connect();

			conn.setConnectTimeout(CONNECT_TIMEOUT);
			conn.setReadTimeout(READ_TIMEOUT);
			Log.i(TAG, conn.getContentType());

			if (!conn.getContentType().startsWith("text/vnd.wap.wml")) {
				return conn.getInputStream();
			} else {
				conn.disconnect();
			}
		}
		return null;
	}
	
	   public static InputStream getURLStream(String weburl) throws IOException {
	        URL url;
	        HttpURLConnection conn;
	        url = new URL(weburl);
	        conn = (HttpURLConnection) url.openConnection();

	        conn.setConnectTimeout(CONNECT_TIMEOUT);
	        conn.setReadTimeout(READ_TIMEOUT);
	        return conn.getInputStream();
	    }

}
