package com.offbye.chinatvguide.rate;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SuggestView;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TVRateActivity extends Activity {
	private static final String TAG = "TVrateActivity";

	private ArrayList<TVRate> pl = new ArrayList<TVRate>();
	private String url = null;
	private ProgressDialog pd;
	private ListView optionsListView;
	private TelephonyManager tm ;
	private String selectdCity;
	private Context mContext;
	private Button mChange;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rate_view);
		mContext = this;
		optionsListView = (ListView) this.findViewById(R.id.ListView01);
		url = buildurl("");
		getDataInitialize();
		
        mChange = (Button) this.findViewById(R.id.change);
        mChange.setText("北京");
        mChange.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new AlertDialog.Builder(mContext).setTitle(R.string.select_dialog).setItems(
                        R.array.tvcity, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                selectdCity = mContext.getResources()
                                        .getStringArray(R.array.tvcity)[which];
                                mChange.setText(selectdCity);
                                url = buildurl(selectdCity);
                                Log.v(TAG, url);
                                getDataInitialize();
                            }
                        }).show();
            }
        });
	}

	
	private String buildurl(String city){
		StringBuffer urlsb =new StringBuffer(128);
		urlsb.append(Constants.url_tvrate);
		urlsb.append("?city=");
		urlsb.append(URLEncoder.encode(city));
		
		if(tm!=null){
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
		}
		
		StringBuffer k = new StringBuffer(128);
		k.append(Constants.key);
		
		urlsb.append("&m=");						
		urlsb.append(MD5.getMD5(k.toString().getBytes()));
		return urlsb.toString();
	}
	
	public ArrayList<TVRate> getTVRatesFormURL(String weburl) {
		ArrayList<TVRate> pl = new ArrayList<TVRate>();
		try {
			String sb =HttpUtil.getUrl(this,weburl);

			if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					TVRate tr = new TVRate();
					tr.rank=jp.getString(1);
					tr.program=jp.getString(2);
					tr.channel=jp.getString(3);
					tr.tvtype=jp.getString(4);
					tr.averageRate=jp.getString(5);
					tr.changes=jp.getString(6);
					pl.add(tr);
				}
			} else {
				progressHandler.sendEmptyMessage(R.string.notify_no_result);
				Log.d(TAG, "response data err");
			}

		} catch (IOException e) {
			progressHandler.sendEmptyMessage(R.string.notify_network_error);
			Log.d(TAG, "network err");
		} catch (JSONException e) {
			progressHandler.sendEmptyMessage(R.string.notify_json_error);
			Log.d(TAG, "decode err");
		} catch (AppException e) {
			progressHandler.sendEmptyMessage(R.string.notify_no_connection);
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
			pl = getTVRatesFormURL(url);
			//Log.v(TAG, "load from web");
			if (pl.size() > 0) {
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
			}

		}

	}
	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				TVRateAdapter pa=new TVRateAdapter(TVRateActivity.this,R.layout.tvrate_row,pl);
        		optionsListView.setAdapter(pa);
        	
				break;
			case R.string.notify_network_error:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(TVRateActivity.this, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(TVRateActivity.this, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(TVRateActivity.this, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(TVRateActivity.this,  R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(TVRateActivity.this, R.string.notify_no_connection, 5).show();
				break;	
			default:
				Toast.makeText(TVRateActivity.this,R.string.notify_no_result, 5).show();
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