package com.offbye.chinatvguide;

import java.util.ArrayList;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SelectChannelView extends Activity {
	private static final String TAG = "SelectChannelView";

	private MydbHelper mydb;

	ListView optionsListView;
	TextView titleText,currentnum;
	ArrayList<TVChannel> channelList;
	int current,score;
	int[] randomQuestions;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelview);

		mydb = new MydbHelper(this);
		titleText = (TextView) this.findViewById(R.id.TextView01);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);
	
		showChannels();
		
		mydb.close();
	}


	private void showChannels() {

		Cursor channellCursor = mydb.fetchChannels();
		startManagingCursor(channellCursor);
		channelList=new ArrayList<TVChannel>();

		while(channellCursor.moveToNext()){
			TVChannel tvchannel=new  TVChannel(
					channellCursor.getString(0),channellCursor.getString(1),
					channellCursor.getString(2),channellCursor.getString(3),
					channellCursor.getString(4),channellCursor.getString(5));
			//Log.i(TAG,channellCursor.getString(2));
			channelList.add(tvchannel);
		}
		channellCursor.close();
		ChannelAdapter channelAdapter=new ChannelAdapter(this,R.layout.channel_row,channelList);

		optionsListView.setAdapter(channelAdapter);
		optionsListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
			{
	            Intent i = new Intent(SelectChannelView.this, ChannelProgramView.class); 
	            i.putExtra("id", id);
	            i.putExtra("channel", channelList.get(position).getChannel());
	            startActivity(i); 
	            //finish();
			}

		});
		
	}
	
	

}