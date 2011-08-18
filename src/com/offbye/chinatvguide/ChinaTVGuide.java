package com.offbye.chinatvguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;

import com.offbye.chinatvguide.grid.Grid;


public class ChinaTVGuide extends Activity {
    private static final String TAG = "ChinaTVGuide";

    private Handler mHandler = new Handler(); 

    ImageView imageview; 
    TextView textview; 
    int alpha = 255; 
    int b = 0; 
    @Override
	public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
        setContentView(R.layout.start); 

        imageview = (ImageView) this.findViewById(R.id.ImageView01); 
        textview = (TextView) this.findViewById(R.id.TextView01); 

        //Log.v(TAG, "ChinaTVGuide start ..."); 
        imageview.setAlpha(alpha); 
        if(PreferencesActivity.isAutoSyncOn(this)){
            this.startService(new Intent(this, SyncService.class));
        }
        new Thread(new Runnable() { 
            public void run() { 
                initApp(); //初始化程序 
                 
                while (b < 2) { 
                    try { 
                        if (b == 0) { 
                            Thread.sleep(2000); 
                            b = 1; 
                        } else { 
                            Thread.sleep(50); 
                        } 

                        updateApp(); 

                    } catch (InterruptedException e) { 
                        e.printStackTrace(); 
                    } 
                } 

            } 
        }).start(); 

        mHandler = new Handler() { 
            @Override 
            public void handleMessage(Message msg) { 
                super.handleMessage(msg); 
                imageview.setAlpha(alpha); 
                imageview.invalidate(); 
            } 
        }; 

    } 

    public void updateApp() { 
        alpha -= 5; 

        if (alpha <= 0) { 
            b = 2; 
            int current=0;
            int score =0;

            Intent i = new Intent(this, Grid.class); 
            i.putExtra("current", current);
            i.putExtra("score", score);
            
            startActivity(i); 
            finish();
        } 

        mHandler.sendMessage(mHandler.obtainMessage()); 

    } 
     
    public void initApp(){ 
         MydbHelper mydb =new MydbHelper(this);
         mydb.FirstStart();
         mydb.close();
    } 
          
}
