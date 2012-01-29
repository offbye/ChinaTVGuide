package com.offbye.chinatvguide.local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

import com.offbye.chinatvguide.ChannelProgramView;
import com.offbye.chinatvguide.MydbHelper;
import com.offbye.chinatvguide.PreferencesActivity;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SyncService;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentList;
import com.offbye.chinatvguide.server.CommentTask;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.MD5;
import com.offbye.chinatvguide.weibo.Post;

public class LocalCurrentProgramView extends Activity {
	private static final String TAG = "LocalCurrentProgramView";
	private String servermsg;
	private MydbHelper mydb;
	private String currentdate, province, city;

	ListView optionsListView;
	TextView titleText, currentnum;

	private ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
	private StringBuffer urlsb = null;
	private StringBuffer sql = null;
	private ProgressDialog pd;

	private TVProgram seletedProgram = null;
	private SharedPreferences prefs;
	private Context  mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.localview);
		mContext = this;

		titleText = (TextView) this.findViewById(R.id.currentlocation);
		optionsListView = (ListView) this.findViewById(R.id.ListView01);

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH:mm");
		currentdate = df.format(date);

		urlsb = new StringBuffer();
		urlsb.append(Constants.getUrlLocal(mContext));
		urlsb.append("?d=");
		urlsb.append(currentdate.substring(0, 8));
		urlsb.append("&t=");
		urlsb.append(currentdate.substring(8));
		urlsb.append("&m=");

		String k = currentdate + Constants.key;
		urlsb.append(MD5.getMD5(k.getBytes()));

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		String tel = tm.getLine1Number();
		if (imei != null) {
			urlsb.append("&imei=");
			urlsb.append(imei);
		}
		if (tel != null) {
			urlsb.append("&tel=");
			urlsb.append(tel);
		}

		sql = new StringBuffer();
		sql.append(" starttime  < '");
		sql.append(currentdate.substring(8));
		sql.append("' and endtime  > '");
		sql.append(currentdate.substring(8));
		sql.append("'");

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		province = prefs.getString(PreferencesActivity.KEY_PROVINCE, "");
		city = prefs.getString(PreferencesActivity.KEY_CITY, "");


		if (TextUtils.isEmpty(province) || TextUtils.isEmpty(city)) {
			showEditCityDialog();
		} else {
			urlsb.append("&province=");
			urlsb.append(URLEncoder.encode(province));
			urlsb.append("&city=");
			urlsb.append(URLEncoder.encode(city));
			getDataInitialize();
		}
	}

	private void showEditCityDialog() {
		LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		final EditText provinceEdit = new EditText(this);
		provinceEdit.setInputType(InputType.TYPE_CLASS_TEXT);
		provinceEdit.setWidth(120);
		provinceEdit.setSingleLine(true);
		provinceEdit.setHint(R.string.hint_province);
		provinceEdit.setText(province);
		final EditText cityEdit = new EditText(this);
		cityEdit.setInputType(InputType.TYPE_CLASS_TEXT);
		cityEdit.setHint(R.string.hint_city);
		cityEdit.setWidth(120);
		provinceEdit.setSingleLine(true);
		cityEdit.setText(city);
		LinearLayout l = new LinearLayout(this);
		l.setGravity(Gravity.CENTER);
		l.setLayoutParams(params);
		l.addView(provinceEdit);
		l.addView(cityEdit);

		AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);
		adBuilder.setTitle(R.string.city_setting);
		adBuilder.setMessage(R.string.input_your_city);
		adBuilder.setView(l);
		adBuilder.setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						city = cityEdit.getText().toString();
						province = provinceEdit.getText().toString();

						prefs.edit().putString(PreferencesActivity.KEY_PROVINCE,
								provinceEdit.getText().toString()).commit();
						prefs.edit().putString(
								PreferencesActivity.KEY_CITY,
								cityEdit.getText().toString()).commit();
						
						Toast.makeText(mContext, String.format(getString(R.string.set_city_ok), province+city), Toast.LENGTH_SHORT).show();
						urlsb.append("&province=");
						urlsb.append(URLEncoder.encode(province));
						urlsb.append("&city=");
						urlsb.append(URLEncoder.encode(city));
						getDataInitialize();
					}
				});

		adBuilder.setNegativeButton(getString(R.string.alert_dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		adBuilder.show();
	}

	public ArrayList<TVProgram> getTVProgramsFormURL(String weburl) {
		ArrayList<TVProgram> pl = new ArrayList<TVProgram>();
		try {
			String sb = HttpUtil.getUrl(this,weburl);

			// 判断是否有新版本下载
			if (sb.length() > 0 && sb.toString().startsWith("newversion")) {
				servermsg = sb.toString();
				progressHandler.sendEmptyMessage(R.string.notify_newversion);
			} else if (sb.length() > 0 && !sb.toString().equals("null")
					&& !sb.toString().equals("error")) {
				JSONArray ja = new JSONArray(sb.toString());
				for (int i = 0; i < ja.length(); i++) {
					JSONArray jp = ja.getJSONArray(i);
					// //Log.v(TAG,"jp.toString()"+jp.toString());
					TVProgram tp = new TVProgram(jp.getString(0), jp
							.getString(1), jp.getString(2), jp.getString(3), jp
							.getString(4), jp.getString(5), jp.getString(6), jp
							.getString(7));
					pl.add(tp);
				}
			} else {
				progressHandler.sendEmptyMessage(R.string.notify_network_error);
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
			mydb = new MydbHelper(mContext);
			// Log.v(TAG, "sql=" + sql.toString());
			Cursor programsCursor = mydb.searchLocalPrograms(sql);

			startManagingCursor(programsCursor);
			while (programsCursor.moveToNext()) {
				TVProgram tvprogram = new TVProgram(
						programsCursor.getString(0), programsCursor
								.getString(1), programsCursor.getString(2),
						programsCursor.getString(3), programsCursor
								.getString(4), programsCursor.getString(5),
						programsCursor.getString(6), programsCursor
								.getString(7));
				pl.add(tvprogram);
			}
			// Log.v(TAG, "load from sqllite");
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
		pd = ProgressDialog.show(this, getString(R.string.msg_loading),
				getString(R.string.msg_wait), true, true);
		pd.setIcon(R.drawable.icon);
		// 2. to start an Thread to do getDataThread working
		GetDataBody myWork = new GetDataBody();
		Thread getDataThread = new Thread(myWork);
		// 3. to call start() to do getDataThread working
		getDataThread.start();
	}

	private class GetDataBody implements Runnable {
		public void run() {
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

	private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				titleText.setText(getString(R.string.current_location)
						+ province + city);

				LocalCurrentProgramAdapter pa = new LocalCurrentProgramAdapter(
						mContext,
						R.layout.local_current_row, pl);
				optionsListView.setAdapter(pa);

				optionsListView
						.setOnItemLongClickListener(new OnItemLongClickListener() {
							public boolean onItemLongClick(AdapterView<?> arg0,
									View arg1, int position, long id) {
								seletedProgram = pl.get(position);

								new AlertDialog.Builder(
										mContext).setTitle(
										R.string.select_dialog).setItems(
										R.array.localoptions,
										new DialogInterface.OnClickListener() {
										    public void onClick(DialogInterface dialog,int which) {
										        
											   if(which == 0){
											       Intent i = new Intent(
                                                           LocalCurrentProgramView.this,
                                                           ChannelProgramView.class);
                                                   i.putExtra("channel", seletedProgram
                                                           .getChannel());
                                                   i.putExtra("channelname", seletedProgram
                                                           .getChannelname().trim());
                                                   i.putExtra("type", "local");

                                                   i.putExtra("province", province);
                                                   i.putExtra("city", city);
                                                   startActivity(i);     
                                                        
			                                   }
			                                   else if (which == 1){
			                                       Intent i = new Intent(Intent.ACTION_VIEW);
			                                       i.putExtra("sms_body", seletedProgram.getChannelname().trim()+"节目"+seletedProgram.getProgram().trim()+ "在"+seletedProgram.getStarttime()+"播出，请注意收看啊");
			                                       i.setType("vnd.android-dir/mms-sms");
			                                       startActivity(i);
			                                   }
			                                   else if (which == 2){
			                                       Intent intent = new Intent();
			                                       intent.setAction(Intent.ACTION_WEB_SEARCH);
			                                       intent.putExtra(SearchManager.QUERY,seletedProgram.getProgram().trim());
			                                       startActivity(intent);
			                                   }
			                                   else if (which == 3){
			                                       Intent intent = new Intent(mContext, CommentList.class);
			                                       intent.putExtra("type", "0");
			                                       intent.putExtra("program", seletedProgram.getProgram().trim());
			                                       startActivity(intent);
			                                   }
			                                   else if (which == 4){
			                                       pd = ProgressDialog.show(mContext, getString(R.string.msg_loading), getString(R.string.msg_wait), true, true);
			                                       pd.setIcon(R.drawable.icon);
			                                       checkin(seletedProgram);
			                                   }
			                                   else if (which == 5){
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
				Toast.makeText(mContext,
						R.string.notify_network_error, 5).show();
				break;
			case R.string.notify_json_error:
				pd.dismiss();
				titleText.setText(R.string.notify_json_error);
				Toast.makeText(mContext,
						R.string.notify_json_error, 5).show();
				break;
			case R.string.notify_database_error:
				pd.dismiss();
				titleText.setText(R.string.notify_database_error);
				Toast.makeText(mContext,
						R.string.notify_database_error, 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				titleText.setText(R.string.notify_no_result);
				Toast.makeText(mContext,
						R.string.notify_no_result, 5).show();
				break;
			case R.string.notify_newversion:
				pd.dismiss();
				titleText.setText(servermsg.split("--")[4]);
				Toast.makeText(mContext,
						servermsg.split("--")[4], 5).show();
				new AlertDialog.Builder(mContext).setIcon(
						R.drawable.icon).setTitle(servermsg.split("--")[3])
						.setMessage(servermsg.split("--")[4])
						.setPositiveButton(R.string.alert_dialog_ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent i = new Intent(
												Intent.ACTION_VIEW,
												Uri
														.parse(servermsg
																.split("--")[2]));
										startActivity(i);
									}
								}).show();

				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(mContext, R.string.notify_no_connection, 5).show();
				break;
			case R.string.notify_cannot_location:
				pd.dismiss();
				titleText.setText(getResources().getText(R.string.no_location));
				Toast.makeText(mContext,
						R.string.no_location, 5).show();
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
				titleText.setText(R.string.notify_network_error);
			    break;
			}
		}
	};
	
	 private void checkin(TVProgram program) {
         Comment c = new Comment();
         c.setChannel(program.getChannelname().trim());
         c.setProgram(program.getProgram().trim());
         c.setType("0");
         if ("".equals(UserStore.getUserId(this))) {
             c.setUserid("guest");
         } else {
             c.setUserid(UserStore.getUserId(this));
         }
         c.setLocation(UserStore.getLocation(mContext));
         c.setLat(UserStore.getLat(mContext));
         c.setLon(UserStore.getLon(mContext));
         c.setScreenName(UserStore.getScreenName(mContext));
         c.setEmail(UserStore.getEmail(mContext));
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
         if (!"".equals(UserStore.getUserId(this))) {
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
		menu.add(0, 0, 0, this.getText(R.string.menu_about)).setIcon(
				R.drawable.icon);
		menu.add(0, 1, 1,  this.getText(R.string.city_setting)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, 2, 2, this.getText(R.string.menu_help)).setIcon(
				android.R.drawable.ic_menu_help);
		menu.add(0, 3, 3, this.getText(R.string.menu_sync)).setIcon(
				R.drawable.ic_menu_refresh);
		menu.add(0, 4, 4, this.getText(R.string.menu_clean)).setIcon(
				android.R.drawable.ic_menu_delete);
		menu.add(0, 5, 5, this.getText(R.string.menu_exit)).setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(
					R.string.menu_abouttitle).setMessage(R.string.aboutinfo)
					.setPositiveButton(this.getText(R.string.alert_dialog_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).show();

			break;
		case 1:
			showEditCityDialog();
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
			MydbHelper mydb = new MydbHelper(this);
			if (mydb.deleteAllPrograms() == true) {
				Toast.makeText(this, R.string.program_data_deleted,
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, R.string.no_data_deleted,
						Toast.LENGTH_LONG).show();
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