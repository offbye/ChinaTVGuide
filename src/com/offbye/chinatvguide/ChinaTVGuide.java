package com.offbye.chinatvguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.util.LocationUtils;



public class ChinaTVGuide extends Activity {
    private static final String TAG = "ChinaTVGuide";

    ImageView imageview; 
    TextView textview; 
    @Override
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.start); 

        imageview = (ImageView) this.findViewById(R.id.ImageView01); 
        textview = (TextView) this.findViewById(R.id.TextView01); 

        new Thread(new Runnable() { 
            public void run() { 
                initApp(); //初始化程序 
                mHandler.sendMessageDelayed(mHandler.obtainMessage(),3000); 
            } 
        }).start(); 
        
        new Thread(){
            public void run() {
                LocationUtils.getLocation(getApplicationContext());
            };
        }.start();


        if(PreferencesActivity.isAutoSyncOn(this)){
            this.startService(new Intent(this, SyncService.class));
        }
    } 
    private Handler mHandler = new Handler() { 
        @Override 
        public void handleMessage(Message msg) { 
            super.handleMessage(msg); 
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
