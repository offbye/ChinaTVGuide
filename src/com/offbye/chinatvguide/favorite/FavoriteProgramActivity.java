package com.offbye.chinatvguide.favorite;

import java.util.ArrayList;
import java.util.Calendar;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.offbye.chinatvguide.MydbHelper;
import com.offbye.chinatvguide.ProgramAdapter;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.TVAlarm;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.util.Shortcut;

public class FavoriteProgramActivity extends Activity {
	private static final String TAG = "FavoriteProgramActivity";
	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private ProgressDialog pd;
	private String servermsg;
	private MydbHelper mydb;
	private ListView optionsListView;
	private TVProgram seletedProgram=null;
	private int seletedinterval=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelview);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		getDataInitialize();
	}

	public ArrayList<TVProgram> getTVProgramsFromDB(String sql) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			mydb = new MydbHelper(FavoriteProgramActivity.this);
			Cursor programsCursor = mydb.getFavoritePrograms();
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
			// 4. the working

			pl = getTVProgramsFromDB("");
			if (pl.size() > 0) {
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
			}
			else 
			{
				progressHandler.sendEmptyMessage(R.string.notify_no_result);
			}
		}

	}

	// Define the Handler that receives messages from the thread calculation
	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				FavouriteProgramAdapter pa=new FavouriteProgramAdapter(FavoriteProgramActivity.this,R.layout.local_current_row,pl);
        		optionsListView.setAdapter(pa);
        		optionsListView.setOnItemClickListener(new OnItemClickListener() {
        			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
        			{
        				seletedProgram = pl.get(position);
        				
        				new AlertDialog.Builder(FavoriteProgramActivity.this)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.alarmtimes)
                        .setSingleChoiceItems(R.array.alarmtimes, 0, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            	 seletedinterval=whichButton;
                            }
                        })
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                /* User clicked Yes so do some stuff */
                   	            Intent indent = new Intent(FavoriteProgramActivity.this, TVAlarm.class); 
                	            indent.putExtra("id", seletedProgram.getId());
                	            indent.putExtra("starttime", seletedProgram.getStarttime());
                	            indent.putExtra("program", seletedProgram.getProgram());
                	            indent.putExtra("channel", seletedProgram.getChannel());
                	            PendingIntent sender = PendingIntent.getBroadcast(FavoriteProgramActivity.this,
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
                                Toast.makeText(FavoriteProgramActivity.this, alarm,10).show();
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
        				
        				new AlertDialog.Builder(FavoriteProgramActivity.this)
        	                .setTitle(R.string.select_dialog)
        	                .setItems(R.array.localoptions, new DialogInterface.OnClickListener() {
        	                    public void onClick(DialogInterface dialog, int which) {

        	                        /* User clicked so do some stuff */
        	                       //String[] items = getResources().getStringArray(R.array.localoptions);
        	                       if(which==0){
        	           	            	Intent i = new Intent(FavoriteProgramActivity.this, FavoriteProgramActivity.class); 
        	           	            	i.putExtra("channel", seletedProgram.getChannel());
        	           					startActivity(i); 
        	                       }
        	                       else if(which==1){
        	                    	   Intent i = new Intent(Intent.ACTION_VIEW);   
        	                    	   i.putExtra("sms_body", seletedProgram.getChannelname().trim()+"节目"+seletedProgram.getProgram().trim()+ "在"+seletedProgram.getStarttime()+"播出，请注意收看啊");   
        	                    	   i.setType("vnd.android-dir/mms-sms");   
        	                    	   startActivity(i);
        	                       }
        	                       else if(which==2){
        	                    	   Intent intent = new Intent();
        	                    	   intent.setAction(Intent.ACTION_WEB_SEARCH);
        	                    	   intent.putExtra(SearchManager.QUERY,seletedProgram.getProgram().trim());
        	                    	   startActivity(intent);
        	                       }
        	                       else if(which==3){
        	                    	   MydbHelper mydb = new MydbHelper(FavoriteProgramActivity.this);
        	                    	   mydb.addFavoriteProgram(seletedProgram.getChannel(), seletedProgram.getDate(), seletedProgram.getStarttime(), seletedProgram.getEndtime(), seletedProgram.getProgram(), seletedProgram.getDaynight(), seletedProgram.getChannelname());
        	           				   mydb.close();
        	           				   Toast.makeText(FavoriteProgramActivity.this, R.string.msg_setfavourate_ok, Toast.LENGTH_LONG).show();
        	                       }
        	                    }
        	                }).show();
						return true;
   
        			}

        		});
				break;
			case R.string.notify_network_error:
				pd.dismiss();
				Toast.makeText(FavoriteProgramActivity.this, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				Toast.makeText(FavoriteProgramActivity.this, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				Toast.makeText(FavoriteProgramActivity.this, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				Toast.makeText(FavoriteProgramActivity.this,  R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();

				Toast.makeText(FavoriteProgramActivity.this, servermsg.split("--")[4], 5).show();
				
				new AlertDialog.Builder(FavoriteProgramActivity.this)
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
				Toast.makeText(FavoriteProgramActivity.this,R.string.notify_no_result, 5).show();
			}
		}
	};


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_home)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.menu_channel)).setIcon(R.drawable.ic_menu_channel);
		menu.add(0, 2, 2,  this.getText(R.string.menu_help)).setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, 3, 3, this.getText(R.string.menu_delete_favourites)).setIcon(android.R.drawable.ic_menu_delete);
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
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle(R.string.menu_delete_favourites)
					.setMessage(R.string.tip_delete_favourites)
					.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							 MydbHelper mydb =new MydbHelper(FavoriteProgramActivity.this);
					         if(mydb.deleteAllFavoritePrograms()==true){
					        	 optionsListView.setAdapter(null);
					        	 Toast.makeText(FavoriteProgramActivity.this, R.string.tip_deleted_favourites, Toast.LENGTH_LONG).show();
					         }
					         else{
					        	 Toast.makeText(FavoriteProgramActivity.this, R.string.tip_not_delete_favourites, Toast.LENGTH_LONG).show();
					         }
					         mydb.close();
						}
					})
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {}

					}).show();
			break;
		case 4:
			this.finish();
			Shortcut.exit(this);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}