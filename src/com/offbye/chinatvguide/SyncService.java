package com.offbye.chinatvguide;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;

public class SyncService extends Service {
	
	public Timer timer;
	public final String TAG = "SyncService";
	private MydbHelper mydb;

	private NotificationManager mNM;
	private String currentdate;
	private StringBuffer urlsb;

	public class SyncServiceBinder extends Binder {
		SyncService getService() {
			return SyncService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mydb = new MydbHelper(this);

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		currentdate = df.format(date);

		urlsb = new StringBuffer();
		urlsb.append(Constants.url_sync);
		urlsb.append("?d=");
		urlsb.append(currentdate);
		
		StringBuffer k = new StringBuffer(128);
		k.append(currentdate);
		k.append(Constants.key);
		
		urlsb.append("&m=");						
		urlsb.append(MD5.getMD5(k.toString().getBytes()));
		
		//构建本地同步url
//		urllocalsb = new StringBuffer();
//		urllocalsb.append(this.getString(R.string.url).replace("s.php", "l.php"));
//		urllocalsb.append("?d=");
//		urllocalsb.append(currentdate);
//		
//		urllocalsb.append("&m=");						
//		urllocalsb.append(MD5.getMD5(k.toString().getBytes()));
		
//		 LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//	        /* 第一次运行向Location Provider取得Location */
//	        Location location = LocationUtil.getLocationProvider(locationManager);
//	        if (location != null) {
//	            GeoPoint point = LocationUtil.getGeoByLocation(location);
//	            Address address = LocationUtil.getAddressbyGeoPoint(SyncService.this, point);
//	            if (address != null) {
//	            	 //Log.v(TAG,address.getAdminArea());
//	            	 city = address.getLocality();
//	            	 urllocalsb.append("&province=");
//	            	 urllocalsb.append(URLEncoder.encode(address.getAdminArea()));
//	            	 urllocalsb.append("&city=");
//	            	 urllocalsb.append(URLEncoder.encode(address.getLocality()));
//
//	            	 //Log.v(TAG,urllocalsb.toString());
//	            	 
//	            	
//	            }
//	        } 
	        

		if (mydb.getProgramsCountByDate(currentdate)> 100) {
			Toast.makeText(this, R.string.havesynced, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, R.string.datasyncing, Toast.LENGTH_SHORT).show();
			Thread thr = new Thread(null, mTask, "SyncService");
			thr.start();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "Starting #" + startId + ": " + intent.getExtras());

	}

	@Override
	public void onDestroy() {
		mydb.close();
		// Cancel the persistent notification.
		mNM.cancel(R.string.synccompleted);

		// Tell the user we stopped.
		Toast.makeText(this, R.string.synccompleted, Toast.LENGTH_SHORT).show();
	}

	Runnable mTask = new Runnable() {
		public void run() {
			
			Log.i(TAG, "getTVProgramsToDb start");
			getTVProgramsToDb(urlsb.toString());
//			if(city!=null){
//				getLocalTVProgramsToDb(urllocalsb.toString());
//			}
		        			
			SyncService.this.stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new SyncServiceBinder();

	private void showNotification() {
		CharSequence text = getText(R.string.synccompleted);
		Notification notification = new Notification(R.drawable.icon, text,
				System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ChannelTab.class), 0);

		notification.setLatestEventInfo(this, getText(R.string.synccompleted),
				text, contentIntent);

		mNM.notify(R.string.synccompleted, notification);
	}

	public void getTVProgramsToDb(String weburl) {
		try {
			String sb = HttpUtil.getUrl(this,weburl);

			if (sb.length() > 10) {  //可能会返回error或null

				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					mydb.insertProgram(jp.getString(1), jp.getString(2), 
							jp.getString(3), jp.getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
				}
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
				Log.d(TAG, "sync success");
			}
			else{
				progressHandler.sendEmptyMessage(R.string.notify_network_error);
				Log.d(TAG, "network return error");
			}
			

		} catch (IOException e) {
			progressHandler.sendEmptyMessage(R.string.notify_network_error);
			Log.d(TAG, "network err");
		} catch (JSONException e) {
			progressHandler.sendEmptyMessage(R.string.notify_json_error);
			Log.d(TAG, "decode err");
		} catch (AppException e) {
			progressHandler.sendEmptyMessage(R.string.notify_no_connection);
		}
	}
	
	public void getLocalTVProgramsToDb(String weburl) {
		try {			
			String sb = HttpUtil.getUrl(this,weburl);

			if (sb.length() > 10) {  //可能会返回error或null

				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					mydb.insertLocalProgram(jp.getString(1), jp.getString(2), 
							jp.getString(3), jp.getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
				}
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
				Log.d(TAG, "sync success");
			}
			else{
				progressHandler.sendEmptyMessage(R.string.notify_network_error);
				Log.d(TAG, "network return error");
			}
			

		} catch (IOException e) {
			progressHandler.sendEmptyMessage(R.string.notify_network_error);
			Log.d(TAG, "network err");
		} catch (JSONException e) {
			progressHandler.sendEmptyMessage(R.string.notify_json_error);
			Log.d(TAG, "decode err");
		} catch (AppException e) {
			progressHandler.sendEmptyMessage(R.string.notify_no_connection);
		}
	}
	
	// Define the Handler that receives messages from the thread
	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				showNotification();
				
				break;
			case R.string.notify_network_error:
				Toast.makeText(SyncService.this, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				Toast.makeText(SyncService.this, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				Toast.makeText(SyncService.this, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				Toast.makeText(SyncService.this, R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_no_connection:
				Toast.makeText(SyncService.this, R.string.notify_no_connection, 5).show();
				break;
			default:
				Toast.makeText(SyncService.this, R.string.notify_network_error, 5).show();
			}
		}
	};
}
