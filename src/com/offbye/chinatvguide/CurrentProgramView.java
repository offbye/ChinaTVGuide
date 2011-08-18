package com.offbye.chinatvguide;

import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentTask;
import com.offbye.chinatvguide.server.user.UserInfoActivity;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;
import com.offbye.chinatvguide.util.Shortcut;
import com.offbye.chinatvguide.weibo.OAuthActivity;
import com.offbye.chinatvguide.weibo.Post;
import com.offbye.chinatvguide.weibo.WeiboCheck;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CurrentProgramView extends Activity {
	private static final String TAG = "CurrentProgramView";
	private Context mContext;
	private String servermsg;
	private String currentdate;

	ListView optionsListView;
	TextView titleText, currentnum;

	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private StringBuffer urlsb  =null;
	private StringBuffer sql = null;
	private ProgressDialog pd;
	private TVProgram seletedProgram=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.current_program);

		mContext = this;
		titleText = (TextView) this.findViewById(R.id.TextView01);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		Date date=new Date();
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMddHH:mm");
		currentdate=df.format(date);

		urlsb = new StringBuffer();
		urlsb.append(Constants.url_broadcast);
		urlsb.append("?d=");
		urlsb.append(currentdate.substring(0, 8));
		urlsb.append("&t=");
		urlsb.append(currentdate.substring(8));
		urlsb.append("&m=");

		String k=currentdate+Constants.key;
		urlsb.append(MD5.getMD5(k.getBytes()));
		//Log.v(TAG,urlsb.toString());

		sql = new StringBuffer();
		sql.append(" date='");
		sql.append(currentdate.substring(0, 8));
		sql.append("' and starttime  < '");
		sql.append(currentdate.substring(8));
		sql.append("' and endtime  > '");
		sql.append(currentdate.substring(8));
		sql.append("'");

		getDataInitialize();
	}

	public ArrayList<TVProgram> getTVProgramsFormURL(String weburl) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			String sb = HttpUtil.getUrl(this,weburl);

			//判断是否有新版本下载
			if (sb.length() > 0 && sb.toString().startsWith("newversion") ){
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
		Cursor programsCursor = null;
		MydbHelper mydb = new MydbHelper(mContext);
		try {
			programsCursor = mydb.searchPrograms(sql);
			while (programsCursor.moveToNext()) {
				TVProgram tvprogram = new TVProgram(
						programsCursor.getString(0), programsCursor
								.getString(1), programsCursor.getString(2),
						programsCursor.getString(3), programsCursor
								.getString(4), programsCursor.getString(5),
								programsCursor.getString(6),programsCursor.getString(7));
				pl.add(tvprogram);
			}
			//Log.v(TAG, "load from sqllite");

		} catch (SQLException e) {
			progressHandler.sendEmptyMessage(R.string.notify_database_error);
			Log.d(TAG, "database err");
			// e.printStackTrace();
		}
		finally{
			if(null != programsCursor)
			{
				programsCursor.close();
			}
			mydb.close();
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

			pl = getTVProgramsFromDB(sql.toString());
			if (pl.size() > 0) {
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
			} else {
				pl = getTVProgramsFormURL(urlsb.toString());
				//Log.v(TAG, "load from web");
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
						mContext, R.layout.current_row, pl);
				optionsListView.setAdapter(pa);

				optionsListView.setOnItemLongClickListener(new OnItemLongClickListener() {
        			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id)
        			{
        				seletedProgram = pl.get(position);
        				new AlertDialog.Builder(mContext)
        	                .setTitle(R.string.select_dialog)
        	                .setItems(R.array.localoptions, new DialogInterface.OnClickListener() {
        	                    public void onClick(DialogInterface dialog, int which) {

        	                       if(which==0){
        	           	            	Intent i = new Intent(mContext, ChannelProgramView.class);
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
        	                    	   MydbHelper mydb = new MydbHelper(mContext);
        	                    	   mydb.addFavoriteProgram(seletedProgram.getChannel(), seletedProgram.getDate(), seletedProgram.getStarttime(), seletedProgram.getEndtime(), seletedProgram.getProgram(), seletedProgram.getDaynight(), seletedProgram.getChannelname());
        	           				   mydb.close();
        	           				   Toast.makeText(mContext, R.string.msg_setfavourate_ok, Toast.LENGTH_LONG).show();
        	                       }
        	                       else if(which == 4){
        	                           pd = ProgressDialog.show(mContext, getString(R.string.msg_loading), getString(R.string.msg_wait), true, true);
                                       pd.setIcon(R.drawable.icon);
                                       checkin(seletedProgram);
                                   }
        	                       else if(which == 5){
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
				titleText.setText(R.string.notify_network_error);
				titleText.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				titleText.setText(R.string.notify_json_error);
				titleText.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				titleText.setText(R.string.notify_database_error);
				titleText.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				titleText.setText(R.string.notify_no_result);
				titleText.setVisibility(View.VISIBLE);
				Toast.makeText(mContext, R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();

				Toast.makeText(mContext, servermsg.split("--")[4], 5).show();

				new AlertDialog.Builder(mContext)
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
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(mContext, R.string.notify_no_connection, 5).show();
				titleText.setText(R.string.notify_no_connection);
				titleText.setVisibility(View.VISIBLE);
				break;
			case R.string.checkin_failed:
                pd.dismiss();
                Toast.makeText(mContext, R.string.checkin_failed, 5).show();
                break;
            case R.string.checkin_succeed:
                pd.dismiss();
                Toast.makeText(mContext, String.format(mContext.getString(R.string.checkin_succeed),Constants.CHECKIN_POINT), 5).show();
                break;
                
			default:
				Toast.makeText(mContext, R.string.notify_network_error, 5).show();
				titleText.setText(R.string.notify_network_error);
				titleText.setVisibility(View.VISIBLE);
			}
		}
	};
	
	 private void checkin(TVProgram program) {
	        Comment c = new Comment();
	        c.setChannel(program.getChannelname());
	        c.setProgram(program.getProgram());
	        c.setType("0");
	        if ("".equals(UserInfoActivity.getUserId(this))) {
	            c.setUserid("guest");
	        } else {
	            c.setUserid(UserInfoActivity.getUserId(this));
	        }
	        String url = CommentTask.genUrl(c);
	        Log.d(TAG, "url:" +url);
	        CommentTask.Callback callback = new CommentTask.Callback() {

	            @Override
	            public void update(int status) {
	                if (status < 0) {
	                    progressHandler.sendMessage(progressHandler.obtainMessage(
	                            R.string.checkin_failed, null));
	                } else {
	                    progressHandler.sendMessage(progressHandler.obtainMessage(
	                            R.string.checkin_succeed, null));
	                }
	            }
	        };
	        new CommentTask(this, url, callback).start();

	         //TODO HOW TO NOTIFY USER WEIBO POST?
	        if (!"".equals(UserInfoActivity.getUserId(this))) {
	            final String msg = mContext.getString(R.string.weibo_watching) + "#"
	                    + program.getChannelname() + "#, #" + program.getProgram() + "#";
	            try {
	                Post.post(mContext, msg);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_home)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.menu_channel)).setIcon(R.drawable.ic_menu_channel);
		menu.add(0, 2, 2,  this.getText(R.string.menu_help)).setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, 3, 3, this.getText(R.string.menu_sync)).setIcon(R.drawable.ic_menu_refresh);
		menu.add(0, 4, 4, this.getText(R.string.menu_clean)).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, 5, 5,  this.getText(R.string.menu_exit)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		if (10 < Integer.valueOf(Build.VERSION.SDK)) {
			menu.getItem(0).setShowAsAction(1);
			menu.getItem(1).setShowAsAction(1);
			menu.getItem(2).setShowAsAction(1);
			menu.getItem(5).setShowAsAction(1);
		}
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
			finish();
			Shortcut.exit(this);
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}