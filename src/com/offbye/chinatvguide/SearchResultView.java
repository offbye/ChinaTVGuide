package com.offbye.chinatvguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;

public class SearchResultView extends Activity {
	private static final String TAG = "SearchResultView";
	private String channel, program, cdate, starttime;
	private boolean notsearchtime = false;
	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private StringBuffer urlsb = null;
	private StringBuffer sql = null;

	private ProgressDialog pd;
	private String servermsg;

	private MydbHelper mydb;
	
	private TVProgram seletedProgram=null;
	private int seletedinterval=0;
	
	ListView optionsListView;
	TextView titleText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelview);

		titleText = (TextView) this.findViewById(R.id.TextView01);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		Bundle extras = getIntent().getExtras();
		channel = extras.getString("channel");
		program = extras.getString("program");
		cdate = extras.getString("cdate");
		starttime = extras.getString("starttime");
		notsearchtime =extras.getBoolean("notsearchtime");
		titleText = (TextView) this.findViewById(R.id.TextView01);

		urlsb = new StringBuffer(128);
		sql = new StringBuffer(128);
		StringBuffer k = new StringBuffer(128);

		urlsb.append(Constants.url);
		urlsb.append("?c=");
		sql.append(" 1=1 ");
		
		if (!channel.equals("") && !channel.equals("all")) {
			sql.append(" and channel='");
			sql.append(channel);
			sql.append("'");

			urlsb.append(channel);
			k.append(channel);
		}
		
		
		if (!cdate.equals("")) {
			sql.append(" and date='");
			sql.append(cdate);
			sql.append("'");
			
			urlsb.append("&d=");
			urlsb.append(cdate);
			k.append(cdate);
		}



		if (!starttime.equals("") && notsearchtime == false) {
			sql.append(" and starttime  < '");
			sql.append(starttime);
			sql.append("' and endtime  > '");
			sql.append(starttime);
			sql.append("' ");

			urlsb.append("&t=");
			urlsb.append(starttime);
			
			k.append(starttime);
		}
		if (!program.equals("")) {
			sql.append(" and program  like '%");
			sql.append(program);
			sql.append("%'");

			urlsb.append("&p=");
			urlsb.append(URLEncoder.encode(program));
			
			k.append(program);
		}
		k.append(Constants.key);
		
		urlsb.append("&m=");						
		urlsb.append(MD5.getMD5(k.toString().getBytes()));
		
		//Log.v(TAG,urlsb.toString());
		//Log.v(TAG,MD5.getMD5("a".getBytes()));
		getDataInitialize();

	}

	public ArrayList<TVProgram> getTVProgramsFormURL(String weburl) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			String sb =HttpUtil.getUrl(this,weburl);

			if (sb.length() > 0 && sb.toString().startsWith("newversion")){
				servermsg=sb.toString();
				progressHandler.sendEmptyMessage(R.string.notify_newversion);
			}
			else if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					TVProgram tp = new TVProgram(jp.getString(0), jp
							.getString(1), jp.getString(2), jp.getString(3), jp
							.getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
					pl.add(tp);
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

	public ArrayList<TVProgram> getTVProgramsFromDB(String sql) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			mydb = new MydbHelper(SearchResultView.this);
			//Log.v(TAG, "sql=" + sql.toString());
			Cursor programsCursor = mydb.searchPrograms(sql);

			startManagingCursor(programsCursor);
			while (programsCursor.moveToNext()) {
				TVProgram tvprogram = new TVProgram(
						programsCursor.getString(0), programsCursor
								.getString(1), programsCursor.getString(2),
						programsCursor.getString(3), programsCursor
								.getString(4), programsCursor.getString(5), programsCursor.getString(6), programsCursor.getString(7));
				pl.add(tvprogram);
			}
			//Log.v(TAG, "load from sqllite");
			mydb.close();
		} catch (SQLException e) {
			progressHandler.sendEmptyMessage(R.string.notify_database_error);
			Log.d(TAG, "database err");
			// e.printStackTrace();
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
			// TODO Auto-generated method stub
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

	// Define the Handler that receives messages from the thread
	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				CurrentProgramAdapter pa = new CurrentProgramAdapter(
						SearchResultView.this, R.layout.current_row, pl);
				optionsListView.setAdapter(pa);
				optionsListView.setOnItemClickListener(new OnItemClickListener() {
        			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
        			{
        				seletedProgram = pl.get(position);
        				
        				new AlertDialog.Builder(SearchResultView.this)
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
                   	            Intent indent = new Intent(SearchResultView.this, TVAlarm.class); 
                	            indent.putExtra("id", seletedProgram.getId());
                	            indent.putExtra("starttime", seletedProgram.getStarttime());
                	            indent.putExtra("program", seletedProgram.getProgram());
                	            indent.putExtra("channel", seletedProgram.getChannel());
                	            PendingIntent sender = PendingIntent.getBroadcast(SearchResultView.this,
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
                	            //Log.v(TAG,"calendar1="+ calendar.getTime());

                	            // Schedule the alarm!
                	            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
                	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
                	            
                	            String alarm= "成功设置提醒 " +seletedProgram.getProgram();
                                Toast.makeText(SearchResultView.this, alarm,10).show();
//                                new AlertDialog.Builder(CurrentProgramView.this)
//                                        .setMessage(alarm)
//                                        .show();
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
        				
        				new AlertDialog.Builder(SearchResultView.this)
        	                .setTitle(R.string.select_dialog)
        	                .setItems(R.array.localoptions, new DialogInterface.OnClickListener() {
        	                    public void onClick(DialogInterface dialog, int which) {
        	                       if(which==0){
        	           	            	Intent i = new Intent(SearchResultView.this, ChannelProgramView.class); 
        	           	            	i.putExtra("channel", seletedProgram.getChannel());
        	           					startActivity(i); 
        	                       }
        	                       else if(which==1){
        	                    	   Intent i = new Intent(Intent.ACTION_VIEW);   
        	                    	   i.putExtra("sms_body", seletedProgram.getChannelname().trim()+"节目"+seletedProgram.getProgram().trim()+ "在"+seletedProgram.getStarttime()+"播出，请注意收看啊");   
        	                    	   i.setType("vnd.android-dir/mms-sms");   
        	                    	   startActivity(i);
        	                       }
        	                       else{
        	                    	   Intent intent = new Intent();
        	                    	   intent.setAction(Intent.ACTION_WEB_SEARCH);
        	                    	   intent.putExtra(SearchManager.QUERY,seletedProgram.getProgram().trim());
        	                    	   startActivity(intent);
        	                       }
        	                    }
        	                }).show();
						return true;
        			}
        		});
				break;
			case R.string.notify_network_error:
				pd.dismiss();
				titleText.setText(R.string.notify_network_error);
				titleText.setVisibility(View.VISIBLE);
				Toast.makeText(SearchResultView.this,R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				titleText.setText(R.string.notify_json_error);
				Toast.makeText(SearchResultView.this, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				titleText.setText(R.string.notify_database_error);
				Toast.makeText(SearchResultView.this, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				titleText.setText(R.string.notify_no_result);
				Toast.makeText(SearchResultView.this, R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(SearchResultView.this, R.string.notify_no_connection, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();

				titleText.setText(servermsg.split("--")[4]);
				Toast.makeText(SearchResultView.this, servermsg.split("--")[4], 5).show();
				
				new AlertDialog.Builder(SearchResultView.this)
                .setIcon(R.drawable.icon)
                .setTitle(servermsg.split("--")[3])
                .setMessage(servermsg.split("--")[4])
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
    
                    	Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(servermsg.split("--")[2]));
        				startActivity(i); 
                    }
                })

                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /* User clicked Cancel so do some stuff */
                    }
                }).show();

				break;		
			default:
				titleText.setText(R.string.notify_no_result);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_about)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.menu_channel)).setIcon(R.drawable.ic_menu_channel);
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
			startActivity(new Intent(this,ChannelTab.class));
			finish();
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