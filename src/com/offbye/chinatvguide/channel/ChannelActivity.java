package com.offbye.chinatvguide.channel;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.offbye.chinatvguide.ChannelAdapter;
import com.offbye.chinatvguide.ChannelProgramView;
import com.offbye.chinatvguide.MydbHelper;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SuggestView;
import com.offbye.chinatvguide.SyncService;
import com.offbye.chinatvguide.TVChannel;
import com.offbye.chinatvguide.server.media.GetMediaListTask;
import com.offbye.chinatvguide.server.media.MediaStore;
import com.offbye.chinatvguide.util.Shortcut;

public class ChannelActivity extends Activity {
	private static final String TAG = "ChannelActivity";
	private MydbHelper mydb;
	ListView optionsListView;
	TextView titleText,currentnum;
	ArrayList<TVChannel> channelList;
	int current,score;
	int[] randomQuestions;
	private String type;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelview);

		mydb = new MydbHelper(this);
		titleText = (TextView) this.findViewById(R.id.TextView01);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		Bundle extras = getIntent().getExtras();
		if(extras!=null)type = extras.getString("type");

		showChannels();
//		ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cwjManager.getActiveNetworkInfo();
//		if (info != null && info.isAvailable()){
//		}
//        else{
//        	new AlertDialog.Builder(ChannelActivity.this)
//			.setIcon(android.R.drawable.ic_dialog_alert)
//			.setTitle(R.string.msg_no_connenction)
//			.setMessage(R.string.msg_no_connenction_detail)
//			.setPositiveButton(R.string.alert_dialog_ok,
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog,
//								int whichButton) {
//
//						}
//					}).show();
//        }
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
		Cursor channellCursor;
		if(!"".equals(type)){
			channellCursor = mydb.fetchChannels(type);
		}
		else{
			channellCursor = mydb.fetchChannels();
		}

		//startManagingCursor(channellCursor);
		channelList=new ArrayList<TVChannel>();

		while(channellCursor.moveToNext()){
			TVChannel tvchannel= new  TVChannel(
					channellCursor.getString(0),channellCursor.getString(1),
					channellCursor.getString(2),channellCursor.getString(3),
					channellCursor.getString(4),channellCursor.getString(5));
			tvchannel.isPlayable = MediaStore.getInstance().isInMediaList(channellCursor.getString(1));
			channelList.add(tvchannel);
		}
		channellCursor.close();
		final ChannelAdapter channelAdapter=new ChannelAdapter(this,R.layout.channel_row,channelList);

		optionsListView.setAdapter(channelAdapter);
		optionsListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
			{
	            Intent i = new Intent(ChannelActivity.this, ChannelProgramView.class);
	            i.putExtra("id", id);
	            i.putExtra("channel", channelList.get(position).getChannel());
	            i.putExtra("channelname", channelList.get(position).getChannelname());
	            i.putExtra("type", channelList.get(position).getType());
	            startActivity(i);
	            //finish();
			}
		});
		optionsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id)
			{
				MydbHelper mydb = new MydbHelper(ChannelActivity.this);
				mydb.toggleFavouriteChannel(channelList.get(position).getChannel(),channelList.get(position).getHidden());
				mydb.close();

				if(arg1.findViewById(R.id.favouriteicon).getVisibility()==View.GONE){
					arg1.findViewById(R.id.favouriteicon).setVisibility(View.VISIBLE);
					channelList.get(position).setHidden("1");
					Toast.makeText(ChannelActivity.this, R.string.msg_setfavourate_ok, Toast.LENGTH_LONG).show();
				}
				else{
					arg1.findViewById(R.id.favouriteicon).setVisibility(View.GONE);
					channelList.get(position).setHidden("0");
					Toast.makeText(ChannelActivity.this, R.string.msg_cancelfavourate_ok, Toast.LENGTH_LONG).show();
				}
				return true;
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

//		if (10 < Integer.valueOf(Build.VERSION.SDK)) {
//			menu.getItem(0).setShowAsAction(1);
//			menu.getItem(1).setShowAsAction(1);
//			menu.getItem(2).setShowAsAction(1);
//			menu.getItem(5).setShowAsAction(1);
//		}
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
			Shortcut.exit(this);
			break;

		}
		return super.onOptionsItemSelected(item);
	}
}