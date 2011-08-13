package com.offbye.chinatvguide;

import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChannelView extends Activity {
	private static final String TAG = "ChannelView";

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
		ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()){
		}
        else{
        	new AlertDialog.Builder(ChannelView.this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.msg_no_connenction)
			.setMessage(R.string.msg_no_connenction_detail)
			.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					}).show();
        }
		mydb.close();
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        mydb = new MydbHelper(this);
        showChannels();
        mydb.close();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
			channelList.add(tvchannel);
		}
		channellCursor.close();
		ChannelAdapter channelAdapter=new ChannelAdapter(this,R.layout.channel_row,channelList);

		optionsListView.setAdapter(channelAdapter);
		optionsListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
			{
	            Intent i = new Intent(ChannelView.this, ChannelProgramView.class); 
	            i.putExtra("id", id);
	            i.putExtra("channel", channelList.get(position).getChannel());
	            startActivity(i); 
	            //finish();
			}
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_about)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.submitsuggest)).setIcon(R.drawable.suggest);
		menu.add(0, 2, 2,  this.getText(R.string.menu_help)).setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, 3, 3, this.getText(R.string.menu_sync)).setIcon(R.drawable.ic_menu_refresh);
		menu.add(0, 4, 4, this.getText(R.string.menu_clean)).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, 5, 5,  this.getText(R.string.menu_exit)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle(R.string.menu_abouttitle)
					.setMessage(R.string.aboutinfo)
					.setPositiveButton(this.getText(R.string.alert_dialog_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).show();

			break;
		case 1:
			startActivity(new Intent(this,SuggestView.class));
			break;
		case 2:
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle(R.string.menu_help)
			.setMessage(R.string.helpinfo)
			.setPositiveButton(this.getText(R.string.alert_dialog_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					}).show();
			break;	
		case 3:
		    this.startService(new Intent(this, SyncService.class));   
			break;
		case 4:
			 MydbHelper mydb =new MydbHelper(this);
	         if(mydb.deleteAllPrograms()==true){
	        	 Toast.makeText(this, R.string.program_data_deleted, Toast.LENGTH_LONG).show();
	         }
	         else{
	        	 Toast.makeText(this, R.string.no_data_deleted, Toast.LENGTH_LONG).show();
	         }
	         mydb.close();

			break;
		case 5:
			this.finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}