package com.offbye.chinatvguide;

import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentTask;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;
import com.offbye.chinatvguide.weibo.OAuthActivity;
import com.offbye.chinatvguide.weibo.Post;

import org.json.JSONArray;
import org.json.JSONException;

import weibo4android.WeiboException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChannelProgramView extends Activity {
	private static final String TAG = "ChannelProgramView";

	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private String sql = null;
	private String url = null;

	private ProgressDialog pd;
	private String servermsg;
	private ListView optionsListView;
	private TextView  cdate;
	private ImageView channellogo;
	private String currentTime;
	private String channel;
	private int currentPosition;

	private TVProgram seletedProgram = null;
	private int seletedinterval=0;
	private TelephonyManager tm ;
	private Date mo,tu,we,th,fr,sa,su;
	private Button btnmo,btntu,btnwe,btnth,btnfr,btnsa,btnsu;
	private Button mCheckin;
	private String channelname;

	private String type;

	private String province;

	private String city;
	private Context mContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channelprogramview);
		mContext = this;
		channellogo = (ImageView) this.findViewById(R.id.channellogo);
		cdate = (TextView) this.findViewById(R.id.cdate);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);
		
		Bundle extras = getIntent().getExtras();
		channel = extras.getString("channel");
		channelname = extras.getString("channelname");
		type = extras.getString("type");
		province = extras.getString("province");
		city = extras.getString("city");
		this.setTitle(channelname);
		
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        currentTime = df.format(date);

		int this_day = date.getDay();
		// 如果周日作为一周的最后一天
		int step_s2 = -this_day+1; // 上周日距离今天的天数（负数表示）
		if (this_day == 0) {
			step_s2 = -6; // 如果今天是周日
		}
		int step_m2 = 7 - this_day; // 周日距离今天的天数（负数表示）
		long thisTime = System.currentTimeMillis();
		

		mo = new Date(thisTime + (long) step_s2 * 24 * 3600 * 1000);
		tu = new Date(thisTime + (long) (step_s2+1) * 24 * 3600 * 1000);
		we = new Date(thisTime + (long) (step_s2+2) * 24 * 3600 * 1000);
		th = new Date(thisTime + (long) (step_s2+3) * 24 * 3600 * 1000);
		fr = new Date(thisTime + (long) (step_s2+4) * 24 * 3600 * 1000);
		sa = new Date(thisTime + (long) (step_s2+5) * 24 * 3600 * 1000);
		su = new Date(thisTime + (long) step_m2 * 24 * 3600 * 1000);
		if (this_day == 0) {
			su = new Date(); // 如果今天是周日
		}
		
		
		tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 

		btnmo = (Button) this.findViewById(R.id.mo);
		btntu = (Button) this.findViewById(R.id.tu);
		btnwe = (Button) this.findViewById(R.id.we);
		btnth = (Button) this.findViewById(R.id.th);
		btnfr = (Button) this.findViewById(R.id.fr);
		btnsa = (Button) this.findViewById(R.id.sa);
		btnsu = (Button) this.findViewById(R.id.su);
		
		//resetWeekbutton();
		//设置今天被选中
		switch(this_day){
			case 0:
				btnsu.setBackgroundResource(R.drawable.tab_selected);
			break;
			case 1:
				btnmo.setBackgroundResource(R.drawable.tab_selected);
			break;
			case 2:
				btntu.setBackgroundResource(R.drawable.tab_selected);
			break;
			case 3:
				btnwe.setBackgroundResource(R.drawable.tab_selected);
			break;
			case 4:
				btnth.setBackgroundResource(R.drawable.tab_selected);
			break;
			case 5:
				btnfr.setBackgroundResource(R.drawable.tab_selected);
			break;
			case 6:
				btnsa.setBackgroundResource(R.drawable.tab_selected);
			break;
		}
			
			
	    showTop(channel,date);
		url = buildurl(channel,date);
		Log.i(TAG, url);
		sql = buildsql(channel,date);
		getDataInitialize();
		
		btnmo.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,mo);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,mo);
				sql = buildsql(channel,mo);
				getDataInitialize();
				
			}
		});
		
		btntu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,tu);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,tu);
				sql = buildsql(channel,tu);
				getDataInitialize();
			}
		});
		
		btnwe.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,we);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,we);
				sql = buildsql(channel,we);
				getDataInitialize();
			}
		});
		btnth.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,th);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,th);
				sql = buildsql(channel,th);
				getDataInitialize();
			}
		});
		
		btnfr.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,fr);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,fr);
				sql = buildsql(channel,fr);
				getDataInitialize();
			}
		});
		
		btnsa.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,sa);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,sa);
				sql = buildsql(channel,sa);
				getDataInitialize();
			}
		});
		
		btnsu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showTop(channel,su);
				resetWeekbutton();
				view.setBackgroundResource(R.drawable.tab_selected);
				url = buildurl(channel,su);
				sql = buildsql(channel,su);
				getDataInitialize();
			}
		});
		
		mCheckin =(Button)findViewById(R.id.checkin);
		mCheckin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                pd = ProgressDialog.show(mContext, getString(R.string.msg_loading), getString(R.string.msg_wait), true, true);
                pd.setIcon(R.drawable.icon);
                String prog = "";
                if(currentPosition > 0 ){
                    prog = pl.get(currentPosition).getProgram().trim();
                }
                checkin(channelname.trim(),prog);
            }
        });
	}
	

    @Override
    protected void onResume() {
        super.onResume();
    }
	private void resetWeekbutton(){
		btnmo.setBackgroundResource(R.xml.btn_week);
		btntu.setBackgroundResource(R.xml.btn_week);
		btnwe.setBackgroundResource(R.xml.btn_week);
		btnth.setBackgroundResource(R.xml.btn_week);
		btnfr.setBackgroundResource(R.xml.btn_week);
		btnsa.setBackgroundResource(R.xml.btn_week);
		btnsu.setBackgroundResource(R.xml.btn_week);
	}
	
	private String buildurl(String channel,Date date){
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		StringBuffer urlsb =new StringBuffer(128);
		if ("sz".equals(type)){
			urlsb.append(Constants.url_shuzi);
		}
		else if("local".equals(type)){
			urlsb.append(Constants.url_local);
		}
		else
		{
			urlsb.append(Constants.url);
		}
		
		urlsb.append("?c=");
		urlsb.append(channel);
		urlsb.append("&d=");
		urlsb.append(df.format(date));
		
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
		k.append(channel);
		k.append(df.format(date));
		k.append(Constants.key);
		
		urlsb.append("&m=");						
		urlsb.append(MD5.getMD5(k.toString().getBytes()));
		
		if("local".equals(type)){
			urlsb.append("&province=");
	    	urlsb.append(URLEncoder.encode(province));
	    	urlsb.append("&city=");
	    	urlsb.append(URLEncoder.encode(city));
		}

		return urlsb.toString();
	}
	private String buildsql(String channel,Date date){
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		StringBuffer sql =new StringBuffer(128);
		sql.append(" channel='");
		sql.append(channel);
		sql.append("'");
		sql.append(" and date='");
		sql.append(df.format(date));
		sql.append("'");
		return  sql.toString();
	}
	
	private void showTop(String channel,Date date){

		SimpleDateFormat df2=new SimpleDateFormat("yyyy年MM月dd日");
		String currentdate2=df2.format(date);
		channellogo.setImageBitmap(getImageFromAssetFile(channel+".png"));
		cdate.setText(currentdate2);
	}

	public ArrayList<TVProgram> getTVProgramsFormURL(String weburl) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			String sb = HttpUtil.getUrl(this,weburl);
			//判断是否有新版本下载
			if (sb.length() > 0 && sb.toString().startsWith("newversion")){
				servermsg=sb.toString();
				progressHandler.sendEmptyMessage(R.string.notify_newversion);
			}
			else if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
				JSONArray ja = new JSONArray(sb.toString());
				int len = ja.length();
				currentPosition = 0;
				for (int i = 0; i < len; i++) {
					JSONArray jp = ja.getJSONArray(i);
					TVProgram tp = new TVProgram(jp.getString(0), jp
							.getString(1), jp.getString(2), jp.getString(3), jp
							.getString(4), jp.getString(5), jp.getString(6), jp.getString(7));
					
	                if (currentPosition == 0 && tp.getStarttime().compareTo(currentTime) < 0
	                        && tp.getEndtime().compareTo(currentTime) > 0) {
	                    tp.setDaynight("c");
	                    currentPosition = i;
	                }
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
		MydbHelper mydb = null;
		try {
			mydb  = new MydbHelper(mContext);
			//Log.v(TAG, "sql=" + sql.toString());
			programsCursor = mydb.searchPrograms(sql);
			startManagingCursor(programsCursor);
			int i = 0;
			currentPosition = 0;
			while (programsCursor.moveToNext()) {
				TVProgram tvprogram = new TVProgram(
						programsCursor.getString(0), programsCursor
								.getString(1), programsCursor.getString(2),
						programsCursor.getString(3), programsCursor
								.getString(4), programsCursor.getString(5), programsCursor.getString(6), programsCursor.getString(7));
                if (currentPosition == 0 && tvprogram.getStarttime().compareTo(currentTime) < 0
                        && tvprogram.getEndtime().compareTo(currentTime) > 0) {
                    tvprogram.setDaynight("c");
                    currentPosition = i;
                }
                pl.add(tvprogram);
				i++;
			}
			//Log.v(TAG, "load from sqllite");
			
		} catch (SQLException e) {
			progressHandler.sendEmptyMessage(R.string.notify_database_error);
			Log.d(TAG, "database err");
			// e.printStackTrace();
		}
		finally{
			if (null != programsCursor){
				programsCursor.close();
			}
			mydb.close();
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

			pl = getTVProgramsFromDB(sql);
			if (pl.size() > 0) {
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
			} else {
				pl = getTVProgramsFormURL(url);
				//Log.v(TAG, "load from web");
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
				ProgramAdapter pa=new ProgramAdapter(mContext,R.layout.program_row,pl);
        		optionsListView.setAdapter(pa);
                if (currentPosition > 0) {
                    Log.d(TAG, "currentPosition=" + currentPosition);
                    optionsListView.setSelection(currentPosition);
                }
        		optionsListView.setOnItemClickListener(new OnItemClickListener() {
        			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
        			{
        				seletedProgram = pl.get(position);
        				
        				new AlertDialog.Builder(mContext)
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
                   	            Intent indent = new Intent(mContext, TVAlarm.class); 
                	            indent.putExtra("id", seletedProgram.getId());
                	            indent.putExtra("starttime", seletedProgram.getStarttime());
                	            indent.putExtra("program", seletedProgram.getProgram());
                	            indent.putExtra("channel", seletedProgram.getChannel());
                	            PendingIntent sender = PendingIntent.getBroadcast(mContext,
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
                                Toast.makeText(mContext, alarm,10).show();
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
        				
        				new AlertDialog.Builder(mContext)
        	                .setTitle(R.string.select_dialog)
        	                .setItems(R.array.programoptions, new DialogInterface.OnClickListener() {
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
				optionsListView.setAdapter(null);
				Toast.makeText(mContext, R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(mContext, R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(mContext, R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				optionsListView.setAdapter(null);
				Toast.makeText(mContext,  R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(mContext, R.string.notify_no_connection, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();
				optionsListView.setAdapter(null);
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
			case R.string.checkin_failed:
                pd.dismiss();
                Toast.makeText(mContext, R.string.checkin_failed, 5).show();
                break;
			case R.string.checkin_succeed:
                pd.dismiss();
                Toast.makeText(mContext, R.string.checkin_succeed, 5).show();
                break;
			default:
				Toast.makeText(mContext,R.string.notify_no_result, 5).show();
			}
		}
	};

    private void checkin(String channel,String program) {
        Comment c = new Comment();
        c.setChannel(channel);
        c.setProgram(program);
        c.setType("0");
        if ("".equals(UserStore.getUserId(this))) {
            c.setUserid("guest");
        } else {
            c.setUserid(UserStore.getUserId(this));
        }
        String url = CommentTask.genUrl(c);
        if(!"".equals(UserStore.getEmail(mContext))){
            url = url + "&email=" + UserStore.getEmail(mContext);
        }
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
        if (!"".equals(UserStore.getUserId(this))) {
            final String msg = mContext.getString(R.string.weibo_watching) + "#"
                    + channel + "#, #" + program + "#";
            try {
                Post.post(mContext, msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
	private Bitmap getImageFromAssetFile(String fileName) {
		Bitmap image = null;
		try {
			AssetManager am = this.getAssets();
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		return image;
	}
	
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