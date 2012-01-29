
package com.offbye.chinatvguide;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;
import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;
import android.app.IntentService;

public class SyncService extends IntentService {
    private static final String TAG = "SyncService";

    private MydbHelper mydb;

    private NotificationManager mNM;

    private String currentdate;

    private StringBuffer urlsb;

    public SyncService() {
        super(TAG);
        Log.d(TAG, " ----> constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        long id = Thread.currentThread().getId();
        Log.d(TAG, " ----> onCreate() in thread id: " + id);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " ----> onDestroy()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, " ----> onStartCommand()");
        // 记录发送此请求的时间
        intent.putExtra("time", System.currentTimeMillis());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        Log.d(TAG, " ----> setIntentRedelivery()");
        super.setIntentRedelivery(enabled);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long id = Thread.currentThread().getId();
        long time = intent.getLongExtra("time", 0);

        Log.d(TAG, " ----> onHandleIntent() in thread id: " + id + "  " + time);

        if (PreferencesActivity.isSyncing(this)) {
            return;
        }

        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return;
        }

        PreferencesActivity.setSyncing(this, true);
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mydb = new MydbHelper(this);

        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        currentdate = df.format(date);

        urlsb = new StringBuffer();
        urlsb.append(Constants.getUrlSync(SyncService.this));
        urlsb.append("?d=");
        urlsb.append(currentdate);

        StringBuffer k = new StringBuffer(128);
        k.append(currentdate);
        k.append(Constants.key);

        urlsb.append("&m=");
        urlsb.append(MD5.getMD5(k.toString().getBytes()));

        if (mydb.getProgramsCountByDate(currentdate) > 100) {
            Toast.makeText(this, R.string.havesynced, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.datasyncing, Toast.LENGTH_LONG).show();
            getTVProgramsToDb(urlsb.toString());
        }

    }

    private void showNotification() {
        CharSequence text = getText(R.string.synccompleted);
        Notification notification = new Notification(R.drawable.icon, text, System
                .currentTimeMillis());
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                ChannelTab.class), 0);
        notification.setLatestEventInfo(this, getText(R.string.synccompleted), text, contentIntent);
        mNM.notify(R.string.synccompleted, notification);
    }

    public void getTVProgramsToDb(String weburl) {
        try {
            String sb = HttpUtil.getUrl(this, weburl);

            if (sb.length() > 10) { // 可能会返回error或null

                JSONArray ja = new JSONArray(sb.toString());
                for (int i = 0; i < ja.length(); i++) {
                    JSONArray jp = ja.getJSONArray(i);
                    mydb.insertProgram(jp.getString(1), jp.getString(2), jp.getString(3), jp
                            .getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
                }
                showNotification();
                Log.d(TAG, "sync success");
            } else {
                Toast.makeText(SyncService.this, R.string.notify_network_error, 5).show();
                Log.d(TAG, "network return error");
            }

        } catch (IOException e) {
            Toast.makeText(SyncService.this, R.string.notify_network_error, 5).show();
            Log.d(TAG, "network err");
        } catch (JSONException e) {
            Toast.makeText(SyncService.this, R.string.notify_json_error, 5).show();
            Log.d(TAG, "decode err");
        } catch (AppException e) {
            Toast.makeText(SyncService.this, R.string.notify_no_connection, 5).show();
        } finally {
            PreferencesActivity.setSyncing(this, false);
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            PreferencesActivity.setSyncToday(this, df.format(new Date()));
        }
    }

}
