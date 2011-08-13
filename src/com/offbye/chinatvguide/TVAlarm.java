package com.offbye.chinatvguide;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.offbye.chinatvguide.channel.ChannelTab;


public class TVAlarm extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		String channel = extras.getString("channel");
		String program = extras.getString("program");
		String starttime = extras.getString("starttime");
		
		String channelname="";
	    MydbHelper mydb = new MydbHelper(context);
	    Cursor c= mydb.fetchChannel(channel);
	    if(c.moveToFirst()){
	    	channelname=c.getString(2);
	    }
		mydb.close();
		
		StringBuffer sb =new StringBuffer(64);
		sb.append(channelname);
		sb.append("节目 ");
		sb.append(program);
		sb.append("在 ");
		sb.append(starttime);
		sb.append("播出，请及时收看");
		
		

		Toast.makeText(context, sb.toString(), 5).show();
	
		NotificationManager manager=(NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);     
		// some messages 表示消息的简单介绍 
		Notification nf = new Notification(R.drawable.icon,program, System.currentTimeMillis()); 
		nf.defaults = Notification.DEFAULT_ALL;
		
//		Uri ringURI = Uri.fromFile(new File("/system/media/audio/ringtones/ringer.mp3"));
//		nf.sound = ringURI;

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, CurrentProgramView.class), 0); 
		// 这里必须设置 LastedEventInfo 否则会出 Exception 

		nf.setLatestEventInfo(context, program, sb.toString(), contentIntent);// 详细的消息标题和内容 
		int uniqueid=(int) System.currentTimeMillis()/1000;
		Log.i("tvalarm","uniqueid="+uniqueid);
		manager.notify(uniqueid, nf);
	}
	

}
