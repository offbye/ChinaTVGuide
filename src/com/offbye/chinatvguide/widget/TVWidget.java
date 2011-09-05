package com.offbye.chinatvguide.widget;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import com.offbye.chinatvguide.CurrentProgramView;
import com.offbye.chinatvguide.MydbHelper;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;

public class TVWidget extends AppWidgetProvider {     

	private static final String TAG = "TVWidget";   
	private Context mContext;
	private ArrayList<TVProgram> pl;
	private Timer timer =null;
    public static final String ACTION_REFRESH = "com.offbye.chinatvguide.widget.TVWidget.refresh";
    private int i =0;
    private AppWidgetManager mWidgetManager = null;
    private int[] mWidgetIds;
    private RemoteViews rv;
    private volatile boolean updated = false;

    @Override
	 public void onReceive(Context context, Intent intent) {
		mContext = context;
		init();
		
		if (intent.getAction().equals(ACTION_REFRESH)) {
			//Log.i("eeeeeeeeeee", "refresh");
			refresh();
		}
		if(null == timer){
    		//Log.i("eeeeeeeeeee", "onReceive new ");
    		refresh();
            timer = new Timer();     
            timer.scheduleAtFixedRate(new TVTask(), 1000, 6000); 
        }
		
	}
    @Override    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,     
            int[] appWidgetIds) {     
    	mContext = context;
    	init();
		refresh();
    	if(null == timer){
    		//Log.i("eeeeeeeeeee", "onUpdate new ");
            timer = new Timer();     
            timer.scheduleAtFixedRate(new TVTask(), 1000, 6000); 
        }
    }
    
	private void refresh() {
		String url = buildUrl();
		if(!"".equals(url)){
			new Thread(){
				public void run(){
					getProgram(buildUrl());
				}
			}.start();
			update(updated);
		}
		else{
			update(false);
		}
	}
	 
    private String getOne() {
		StringBuilder re = new StringBuilder();
		if (null != pl && i < pl.size()) {
			TVProgram tp = pl.get(i);
			re.append(tp.getChannelname().trim()).append("   ").append(
					tp.getStarttime()).append("--").append(tp.getEndtime())
					.append("\n").append(tp.getProgram().trim());
			i++;
		} else {
			i = 0;
			if (null != pl && pl.size() >0){
				TVProgram tp = pl.get(i);
				re.append(tp.getChannelname().trim()).append("   ").append(
						tp.getStarttime()).append("--").append(tp.getEndtime())
						.append("\n").append(tp.getProgram().trim());
			}
		}
		return re.toString();
	}
    
    private void init(){
    	mWidgetManager = AppWidgetManager.getInstance(mContext);
        mWidgetIds = mWidgetManager.getAppWidgetIds(new ComponentName(mContext, TVWidget.class));
        rv = new RemoteViews(mContext.getPackageName(),
        		 R.layout.widget);
        
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
				0, new Intent(mContext, CurrentProgramView.class), 0);
        rv.setOnClickPendingIntent(R.id.hello, contentIntent);
        
        Intent bcast = new Intent(mContext, TVWidget.class);
        bcast.setAction(ACTION_REFRESH);
        PendingIntent pending = PendingIntent.getBroadcast(
        		mContext, 0, bcast, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setOnClickPendingIntent(R.id.refresh, pending);
    }
   
    private void update(boolean refreshed) {        
        if(refreshed){
            rv.setTextViewText(R.id.hello, getOne());
    		//Log.i("eeeeeeeeeee", "update one ");
        }
        else{
        	rv.setTextViewText(R.id.hello, mContext.getString(R.string.widget_info));
    		//Log.i("eeeeeeeeeee", "update info ");
        }
		
        for (int i : mWidgetIds) {
            mWidgetManager.updateAppWidget(i, rv);
        }
        //Log.i("eeeeeeeeeee", "update finish ");
    }
    
    
    private class TVTask extends TimerTask{     
        private static final String TAG = "TVTask";
        public TVTask(){     
        }     
        public void run() {
        	
        	update(updated);
        }
    }
    
    private String buildUrl(){
    	MydbHelper db =new MydbHelper(mContext);
    	String sqlargs = db.getFavs();
    	db.close();
    	if("".equals(sqlargs)){
    		return "";
    	}
    	Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHH:mm");
		String currentdate=df.format(date);
		String channel = "";
		StringBuilder urlsb = new StringBuilder();
		urlsb.append(Constants.url);
		urlsb.append("?c=");
		urlsb.append(channel);
		urlsb.append("&fav=");
		urlsb.append(sqlargs);
		urlsb.append("&d=");
		urlsb.append(currentdate.substring(0, 8));
		urlsb.append("&t=");
		urlsb.append(currentdate.substring(8));
		urlsb.append("&m=");
		
		String k= channel+currentdate+Constants.key;
		urlsb.append(MD5.getMD5(k.getBytes()));
		
		return  urlsb.toString();
		}

    private String getProgram(String weburl){
    	try {
    		//Log.i(TAG,weburl);
			String sb = HttpUtil.getUrl(mContext,weburl);
			pl = new ArrayList<TVProgram>();
			StringBuilder re = new StringBuilder();
			if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					TVProgram tp = new TVProgram(jp.getString(0), jp
							.getString(1), jp.getString(2), jp.getString(3), jp
							.getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
					pl.add(tp);
					re.append(tp.getChannelname().trim()).append(": ")
					.append(tp.getProgram().trim()).append(" ")
					.append(tp.getStarttime()).append("--")
					.append(tp.getEndtime()).append("\n");
				}
				Log.d(TAG, "success");
				updated = true;
				return "success";

			} else {
				updated = false;
				Log.d(TAG, "response data err");
				return mContext.getString(R.string.notify_no_result);
			}

		} catch (IOException e) {
			Log.d(TAG, "network err");
			return mContext.getString(R.string.notify_network_error);
		} catch (JSONException e) {
			Log.d(TAG, "decode err");
			return mContext.getString(R.string.notify_json_error);
		} catch (AppException e) {
			return mContext.getString(R.string.notify_no_connection);
		}
    }           
}    
