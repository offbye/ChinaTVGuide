package com.offbye.chinatvguide.rss;

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
import com.offbye.chinatvguide.MydbHelper;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SuggestView;
import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.grid.Grid;

public class RSSActivity extends Activity {
	private static final String TAG = "RSSActivity";
	ListView optionsListView;
	TextView titleText;
	ArrayList<RSSChannel> channelList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rsschannelview);

		titleText = (TextView) this.findViewById(R.id.TextView01);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		showChannels();
		ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
		} else {
			new AlertDialog.Builder(RSSActivity.this).setIcon(
					android.R.drawable.ic_dialog_alert).setTitle(
					R.string.msg_no_connenction).setMessage(
					R.string.msg_no_connenction_detail).setPositiveButton(
					R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					}).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		showChannels();
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
		MydbHelper mydb = null;
		Cursor channellCursor = null;
		try {
			mydb = new MydbHelper(this);
			channellCursor = mydb.fetchRSSChannels();

			//startManagingCursor(channellCursor);
			channelList = new ArrayList<RSSChannel>();

			while (channellCursor.moveToNext()) {
				RSSChannel channel = new RSSChannel();
				channel.url = channellCursor.getString(1);
				channel.title = channellCursor.getString(2);
				channel.image = channellCursor.getString(3);
				channel.hidden = channellCursor.getInt(4);
				channel.item_count = channellCursor.getInt(5);
				channel.description_count = channellCursor.getInt(6);
				channel.category = channellCursor.getString(7);
				channelList.add(channel);
			}
		} finally {
			if (null != channellCursor) {
				channellCursor.close();
			}
			if (null != mydb) {
				mydb.close();
			}
		}

		RSSChannelAdapter channelAdapter = new RSSChannelAdapter(this,
				R.layout.rsschannel_row, channelList);

		optionsListView.setAdapter(channelAdapter);
		optionsListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Intent i = new Intent(RSSActivity.this, RSSReader.class);
				i.putExtra("url", channelList.get(position).url);
				i.putExtra("title", channelList.get(position).title);
				i.putExtra("item_count", channelList.get(position).item_count);
				i.putExtra("description_count",
						channelList.get(position).description_count);
				startActivity(i);
				// finish();
			}
		});
		optionsListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int position, long id) {
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_home)).setIcon(
				R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.menu_channel)).setIcon(
				R.drawable.ic_menu_channel);
		menu.add(0, 2, 2, this.getText(R.string.menu_help)).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(0, 3, 3, this.getText(R.string.grid_suggest)).setIcon(
				R.drawable.suggest);
		menu.add(0, 4, 4, this.getText(R.string.menu_exit)).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
//		if (10 < Integer.valueOf(Build.VERSION.SDK)) {
//			menu.getItem(0).setShowAsAction(1);
//			menu.getItem(1).setShowAsAction(1);
//			menu.getItem(2).setShowAsAction(1);
//		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			startActivity(new Intent(this, Grid.class));
			finish();
			break;
		case 1:
			startActivity(new Intent(this, ChannelTab.class));
			finish();
			break;
		case 2:
			new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(
					R.string.menu_help).setMessage(R.string.helpinfo)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).show();
			break;
		case 3:
			startActivity(new Intent(this, SuggestView.class));
			break;
		case 4:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}