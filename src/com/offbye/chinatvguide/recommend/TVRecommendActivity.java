package com.offbye.chinatvguide.recommend;


import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SuggestView;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class TVRecommendActivity extends Activity {
	private static final String TAG = "TVRecommendActivity";
	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private ProgressDialog pd;
	private String servermsg;
	private ListView optionsListView;
	private TVProgram seletedProgram=null;
	private StringBuffer urlsb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelview);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		urlsb = new StringBuffer();
		urlsb.append(Constants.url_tvrecommend);
		urlsb.append("?m=");
		urlsb.append(MD5.getMD5(Constants.key.getBytes()));

		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		String tel = tm.getLine1Number();
		if(imei!=null){
			urlsb.append("&imei=");
			urlsb.append(imei);
		}
		if(tel!=null){
			urlsb.append("&tel=");
			urlsb.append(tel);
		}

		getDataInitialize();
	}
	public ArrayList<TVProgram> getTVProgramsFormURL(String weburl) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			String sb = HttpUtil.getUrl(this,weburl);

			if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					// //Log.v(TAG,"jp.toString()"+jp.toString());
					TVProgram tp = new TVProgram(jp.getString(0), jp
							.getString(1), jp.getString(2), jp.getString(3), jp
							.getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
					pl.add(tp);
				}
			} else {
				mHandler.sendEmptyMessage(R.string.notify_newversion);
				Log.d(TAG, "response data err");
			}

		} catch (IOException e) {
			mHandler.sendEmptyMessage(R.string.notify_network_error);
			Log.d(TAG, "network err");
		} catch (JSONException e) {
			mHandler.sendEmptyMessage(R.string.notify_json_error);
			Log.d(TAG, "decode err");
		} catch (AppException e) {
			mHandler.sendEmptyMessage(R.string.notify_no_connection);
		}
		return pl;
	}

	public void getDataInitialize() {
		// 1. to start ProgressDialog
		pd = ProgressDialog.show(this, getString(R.string.msg_loading), getString(R.string.msg_wait), true, false);
		pd.setIcon(R.drawable.icon);
		// 2. to start an Thread to do getDataThread working
		GetDataBody myWork = new GetDataBody();
		Thread getDataThread = new Thread(myWork);
		// 3. to call start() to do getDataThread working
		getDataThread.start();
	}

	private class GetDataBody implements Runnable {
		public void run() {
			// 4. the working

			pl = getTVProgramsFormURL(urlsb.toString());
			if (pl.size() > 0) {
				mHandler.sendEmptyMessage(R.string.notify_succeeded);
			}
			else
			{
				mHandler.sendEmptyMessage(R.string.notify_no_result);
			}
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				TVRecommendAdapter pa=new TVRecommendAdapter(TVRecommendActivity.this,R.layout.local_current_row,pl);
        		optionsListView.setAdapter(pa);

				break;
			case R.string.notify_network_error:
				pd.dismiss();
				Toast.makeText(TVRecommendActivity.this, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				Toast.makeText(TVRecommendActivity.this, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				Toast.makeText(TVRecommendActivity.this, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				Toast.makeText(TVRecommendActivity.this,  R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(TVRecommendActivity.this, R.string.notify_no_connection, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();

				Toast.makeText(TVRecommendActivity.this, servermsg.split("--")[4], 5).show();

				new AlertDialog.Builder(TVRecommendActivity.this)
                .setIcon(R.drawable.icon)
                .setTitle(servermsg.split("--")[3])
                .setMessage(servermsg.split("--")[4])
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(servermsg.split("--")[2]));
        				startActivity(i);
                    }
                }).show();

				break;
			default:
				Toast.makeText(TVRecommendActivity.this,R.string.notify_no_result, 5).show();
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_home)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.menu_channel)).setIcon(R.drawable.ic_menu_channel);
		menu.add(0, 2, 2,  this.getText(R.string.menu_help)).setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, 3, 3, this.getText(R.string.grid_suggest)).setIcon(R.drawable.suggest);
		menu.add(0, 4, 4,  this.getText(R.string.menu_exit)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
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
			startActivity(new Intent(this,Grid.class));
			finish();
			break;
		case 1:
			startActivity(new Intent(this,ChannelTab.class));
			finish();
			break;
		case 2:
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle(R.string.menu_help)
			.setMessage(R.string.helpinfo)
			.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					})
					.show();
			break;
		case 3:
			startActivity(new Intent(this,SuggestView.class));
			break;
		case 4:
			this.finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}