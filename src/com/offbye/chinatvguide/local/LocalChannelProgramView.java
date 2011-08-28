package com.offbye.chinatvguide.local;

import com.offbye.chinatvguide.MydbHelper;
import com.offbye.chinatvguide.PreferencesActivity;
import com.offbye.chinatvguide.ProgramAdapter;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SyncService;
import com.offbye.chinatvguide.TVAlarm;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;
import com.offbye.chinatvguide.weibo.Post;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LocalChannelProgramView extends Activity {
	private static final String TAG = "LocalChannelProgramView";

	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private StringBuffer sql = null;
	private StringBuffer urlsb = null;
	private ProgressDialog pd;
	private String servermsg;
	
	private MydbHelper mydb;

	private ListView optionsListView;
	private TextView  cdate;
	private String currentdate;
	private String channel;
	private String channelname,province,city;

	private TVProgram seletedProgram=null;
	private int seletedinterval=0;
	private Context mContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelview);

		mContext = this;

		cdate = (TextView) this.findViewById(R.id.TextView01);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);
		Bundle extras = getIntent().getExtras();
		
		channel = extras.getString("channel");
		channelname = extras.getString("channelname");
		this.setTitle(channelname);
		province = extras.getString("province");
		city = extras.getString("city");
		
		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		currentdate=df.format(date);
		SimpleDateFormat df2=new SimpleDateFormat("yyyy年MM月dd日");
		String currentdate2=df2.format(date);

		cdate.setText(channelname+currentdate2+"节目");

		
		
		urlsb =new StringBuffer(128);
		urlsb.append(Constants.url_local);

		urlsb.append("?c=");
		urlsb.append(channel);
		urlsb.append("&d=");
		urlsb.append(currentdate);
		
		sql =new StringBuffer(128);
		sql.append(" channel='");
		sql.append(channel);
		sql.append("'");
		sql.append(" and date='");
		sql.append(currentdate);
		sql.append("'");
		
		StringBuffer k = new StringBuffer(128);
		k.append(channel);
		k.append(currentdate);
		k.append(Constants.key);
		
		urlsb.append("&m=");						
		urlsb.append(MD5.getMD5(k.toString().getBytes()));
		
		urlsb.append("&province=");
    	urlsb.append(URLEncoder.encode(province));
    	urlsb.append("&city=");
    	urlsb.append(URLEncoder.encode(city));
    	 
		//Log.v(TAG,urlsb.toString());
		getDataInitialize();

	}

	public ArrayList<TVProgram> getTVProgramsFormURL(String weburl) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			
			String body =HttpUtil.getUrl(this,weburl);

			if (body.length() > 0 && body.startsWith("newversion")){
				servermsg=body.toString();
				progressHandler.sendEmptyMessage(R.string.notify_newversion);
			}
			else if (body.length() > 0 && body.startsWith("null")){
				progressHandler.sendEmptyMessage(R.string.notify_no_result);
			}
			else if (body.length() > 0 && body.startsWith("error")){
				progressHandler.sendEmptyMessage(R.string.notify_no_permision);
			}
			else if (body.length() > 0 && !body.equals("null") && !body.equals("error")) {
				JSONArray ja = new JSONArray(body);
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					TVProgram tp = new TVProgram(jp.getString(0), jp
							.getString(1), jp.getString(2), jp.getString(3), jp
							.getString(4), jp.getString(5), jp.getString(6));
					pl.add(tp);
				}
			} else {
				progressHandler.sendEmptyMessage(R.string.notify_no_result);
				//Log.d(TAG, "response data err");
			}

		} catch (IOException e) {
			progressHandler.sendEmptyMessage(R.string.notify_network_error);
			//Log.d(TAG, "network err");
		} catch (JSONException e) {
			progressHandler.sendEmptyMessage(R.string.notify_json_error);
			//Log.d(TAG, "decode err");
		} catch (AppException e) {
			progressHandler.sendEmptyMessage(R.string.notify_no_connection);
		}
		return pl;
	}

	public ArrayList<TVProgram> getTVProgramsFromDB(String sql) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			mydb = new MydbHelper(LocalChannelProgramView.this);
			//Log.v(TAG, "sql=" + sql.toString());
			Cursor programsCursor = mydb.searchLocalPrograms(sql);

			startManagingCursor(programsCursor);
			while (programsCursor.moveToNext()) {
				TVProgram tvprogram = new TVProgram(
						programsCursor.getString(0), programsCursor
								.getString(1), programsCursor.getString(2),
						programsCursor.getString(3), programsCursor
								.getString(4), programsCursor.getString(5), programsCursor.getString(6));
				pl.add(tvprogram);
			}
			//Log.v(TAG, "load from sqllite");
			mydb.close();
		} catch (SQLException e) {
			progressHandler.sendEmptyMessage(R.string.notify_database_error);
			//Log.d(TAG, "database err");
			// e.printStackTrace();
		}
		return pl;
	}

	public void getDataInitialize() {
		// 1. to start ProgressDialog
		pd = ProgressDialog.show(this, getString(R.string.msg_loading), getString(R.string.msg_wait), true, true);
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

			pl = getTVProgramsFromDB(sql.toString());
			if (pl.size() > 0) {
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
			} else {
				pl = getTVProgramsFormURL(urlsb.toString());
				if (pl.size() > 0) {
					progressHandler.sendEmptyMessage(R.string.notify_succeeded);
				}
			}

		}

	}


	// Define the Handler that receives messages from the thread calculation
	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				ProgramAdapter pa=new ProgramAdapter(LocalChannelProgramView.this,R.layout.program_row,pl);
        		optionsListView.setAdapter(pa);
        		optionsListView.setOnItemClickListener(new OnItemClickListener() {
        			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
        			{
        				seletedProgram = pl.get(position);
        				
        				new AlertDialog.Builder(LocalChannelProgramView.this)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.alarmtimes)
                        .setSingleChoiceItems(R.array.alarmtimes, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked on a radio button do some stuff */
                            	 //Log.v(TAG,"setSingleChoiceItems="+whichButton);
                            	 seletedinterval=whichButton;
                            }
                        })
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked Yes so do some stuff */
                   	            Intent indent = new Intent(LocalChannelProgramView.this, TVAlarm.class); 
                	            indent.putExtra("id", seletedProgram.getId());
                	            indent.putExtra("starttime", seletedProgram.getStarttime());
                	            indent.putExtra("program", seletedProgram.getProgram());
                	            indent.putExtra("channel", seletedProgram.getChannel());
                	            PendingIntent sender = PendingIntent.getBroadcast(LocalChannelProgramView.this,
                	                    0, indent,PendingIntent.FLAG_CANCEL_CURRENT);

                	            Calendar calendar = Calendar.getInstance();
                	            calendar.setTimeInMillis(System.currentTimeMillis());

                	            String starttime = seletedProgram.getStarttime();
                	            int year = Integer.parseInt(seletedProgram.getDate().substring(0,4));
                	            int month = Integer.parseInt(seletedProgram.getDate().substring(4,6));
                	            int day = Integer.parseInt(seletedProgram.getDate().substring(6,8));
                	            int hourOfDay=Integer.parseInt(starttime.split(":")[0]);
                	            int minute=Integer.parseInt(starttime.split(":")[1]); 
                	            
                	            calendar.set(year, month-1, day, hourOfDay, minute);
                	            
                	            if(seletedinterval==0){
                	            	calendar.add(Calendar.MINUTE, 0);
                	            }
                	            if(seletedinterval==1){
                	            	calendar.add(Calendar.MINUTE, -10);
                	            }
                	            if(seletedinterval==2){
                	            	calendar.add(Calendar.HOUR, -1);
                	            }
                	            if(seletedinterval==3){
                	            	calendar.add(Calendar.HOUR, -3);
                	            }
                	            //0-6点节目是明天的,加24小时
                	            if(hourOfDay>=0 && hourOfDay<6){
                	            	calendar.add(Calendar.HOUR, 24);
                	            }
                	            
                	            // Schedule the alarm!
                	            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                	            
                	            String alarm= "成功设置提醒 " +seletedProgram.getProgram();
                                Toast.makeText(LocalChannelProgramView.this, alarm,10).show();
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked No so do some stuff */
                            }
                        }).show();
   
        			}
        		});
        		
        		optionsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
        			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id)
        			{
        				seletedProgram = pl.get(position);
        				
        				new AlertDialog.Builder(LocalChannelProgramView.this)
        	                .setTitle(R.string.select_dialog)
        	                .setItems(R.array.localoptions, new DialogInterface.OnClickListener() {
        	                    public void onClick(DialogInterface dialog, int which) {
        	                        if(which == 0){
                                        Intent i = new Intent(Intent.ACTION_VIEW);   
                                        i.putExtra("sms_body", seletedProgram.getChannelname().trim()+"节目"+seletedProgram.getProgram().trim()+ "在"+seletedProgram.getStarttime()+"播出，请注意收看啊");   
                                        i.setType("vnd.android-dir/mms-sms");   
                                        startActivity(i);
                                    }
                                    else if(which == 1){
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_WEB_SEARCH);
                                        intent.putExtra(SearchManager.QUERY,seletedProgram.getProgram().trim());
                                        startActivity(intent);
                                    }
                                    else if(which == 2){
                                        MydbHelper mydb = new MydbHelper(mContext);
                                        mydb.addFavoriteProgram(seletedProgram.getChannel(), seletedProgram.getDate(), seletedProgram.getStarttime(), seletedProgram.getEndtime(), seletedProgram.getProgram(), seletedProgram.getDaynight(), seletedProgram.getChannelname());
                                        mydb.close();
                                        Toast.makeText(mContext, R.string.msg_setfavourate_ok, Toast.LENGTH_LONG).show();
                                    }
                                    else if(which == 3){
                                        Post.addWeibo(mContext, seletedProgram);
                                    }
        	                    }
        	                }).show();
						return true;
   
        			}

        		});
				break;
			case R.string.notify_network_error:
				pd.dismiss();
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_no_permision:
				pd.dismiss();
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_no_permision, 5).show();
				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_no_connection, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();

				Toast.makeText(LocalChannelProgramView.this, servermsg.split("--")[4], 5).show();
				
				new AlertDialog.Builder(LocalChannelProgramView.this)
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
				Toast.makeText(LocalChannelProgramView.this, R.string.notify_no_result, 5).show();
			}
		}
	};


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_about)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1,  this.getText(R.string.preferences_name)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, 2, 2, this.getText(R.string.menu_help)).setIcon(
				android.R.drawable.ic_menu_help);
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
			startActivity(new Intent(this,PreferencesActivity.class));
			finish();
			break;
		case 2:
			new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(
					R.string.menu_help).setMessage(R.string.helpinfo)
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