package com.offbye.chinatvguide;

import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.LocationUtils;
import com.offbye.chinatvguide.weibo.OAuthConstant;

import weibo4android.Weibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;



public class ChinaTVGuide extends Activity {
    private static final String TAG = "ChinaTVGuide";

    ImageView imageview; 
    TextView textview;

    private Context mContext; 
    @Override
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.start); 

        mContext = this;
        imageview = (ImageView) this.findViewById(R.id.ImageView01); 
        textview = (TextView) this.findViewById(R.id.TextView01); 

        new Thread(new Runnable() { 
            public void run() { 
                initApp(); //初始化程序 
                mHandler.sendMessageDelayed(mHandler.obtainMessage(),2000); 
            } 
        }).start(); 
        
        new Thread(){
            public void run() {
                try {
                    if (5 < Integer.valueOf(Build.VERSION.SDK)) {
                        LocationUtils.getLocation(getApplicationContext());
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            };
        }.start();

        final String token = UserStore.getAccessToken(mContext);
        final String secret = UserStore.getAccessSecret(mContext);
        if (token.length() > 0 && secret.length() > 0){
            new Thread() {
                public void run() {
                    try {
                        final Weibo weibo = OAuthConstant.getInstance().getWeibo();
                        OAuthConstant.getInstance().setToken(token);
                        OAuthConstant.getInstance().setTokenSecret(secret);
                        weibo.setToken(token, secret);
                        weibo4android.User u = weibo.verifyCredentials();
                        if (null != u) {
                            UserStore.setScreenName(mContext, u.getScreenName());
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                };
            }.start(); 
        }

    } 
    private Handler mHandler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
            super.handleMessage(msg); 
            Date date=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String today = df.format(date);
            
            if(PreferencesActivity.isAutoSyncOn(ChinaTVGuide.this) 
                    && !PreferencesActivity.getSyncToday(ChinaTVGuide.this).equals(today)){
                Log.d(TAG, "SyncService");
                startService(new Intent(ChinaTVGuide.this, SyncService.class));
                PreferencesActivity.setSyncToday(ChinaTVGuide.this, today);
            }
            updateApp();
        } 
    }; 

    public void updateApp() { 
            Intent i = new Intent(this, Grid.class); 
            startActivity(i); 
            finish();
    }
     
    public void initApp(){ 
         MydbHelper mydb =new MydbHelper(this);
         mydb.FirstStart();
         mydb.close();
    } 
          
}
