package com.offbye.chinatvguide;

import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.Validator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.URLEncoder;

public class SuggestView extends Activity {
	private static final String TAG = "SuggestView";

	private StringBuffer urlsb = null;
	private ProgressDialog pd;
	private EditText content,email;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggest);
        
        this.getWindow().setBackgroundDrawableResource(R.drawable.home);
        
        content = (EditText) this.findViewById(R.id.content);
        email = (EditText) this.findViewById(R.id.email);
        
    	urlsb =new StringBuffer(128);
		urlsb.append(Constants.url_suggest);
		
		ConnectivityManager cwjManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()){
		}
        else{
        	new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.msg_no_connenction)
			.setMessage(R.string.msg_no_connenction_detail)
			.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					}).show();
        }
		
		
		Button submit = (Button) this.findViewById(R.id.submit);
		submit.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					if (content.getText().toString().length() < 5) {
						showMsgBox(R.string.invalid_content_title,
								R.string.invalid_content_msg);
					}

					else if (!Validator.isEmail(email.getText().toString())) {
						showMsgBox(R.string.invalid_email_title,
								R.string.invalid_email_msg);
					} else {
						urlsb.append("?c=");
						urlsb.append(URLEncoder
								.encode(content.getText().toString()));
						urlsb.append("&e=");
						urlsb.append(URLEncoder.encode(email.getText().toString()));
						// Log.v(TAG,urlsb.toString());
						getDataInitialize();
					}
					
				}
			});
		        
    }
	public void showMsgBox(int title, int msg) {
		new AlertDialog.Builder(SuggestView.this).setIcon(
				android.R.drawable.ic_dialog_alert).setTitle(title).setMessage(
				msg).setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				}).show();
	}

	private  String saveSuggest(String weburl) {
    	String re="";
		try {
			re = HttpUtil.getUrl(this,weburl);

		} catch (IOException e) {
			progressHandler.sendEmptyMessage(R.string.notify_network_error);
			Log.d(TAG, "network err");
		} catch (AppException e) {
			progressHandler.sendEmptyMessage(R.string.notify_no_connection);
		} 
		return re;
	}

	private void getDataInitialize() {
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
			String re= saveSuggest(urlsb.toString());
			//Log.v(TAG, "load from web");
			if (re.equals("ok")) {
				// 5. to call sendEmptyMessage() to notificate this working
				// is done
				progressHandler.sendEmptyMessage(R.string.notify_succeeded);
			}
			else{
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
				Toast.makeText(SuggestView.this, "成功提交建议", 5).show();
				content.setText("");
				break;
			case R.string.notify_network_error:
				pd.dismiss();
				//Log.v(TAG, "R.string.notify_network_error");
				Toast.makeText(SuggestView.this, "网络传输错误", 5).show();
				break;
			case R.string.notify_no_result:
				pd.dismiss();
				//Log.v(TAG, "R.string.notify_no_result");
				Toast.makeText(SuggestView.this, "提交建议失败", 5).show();
				break;
			case R.string.notify_no_connection:
				pd.dismiss();
				Toast.makeText(SuggestView.this, R.string.notify_no_connection, 5).show();
				break;
			default:
				Toast.makeText(SuggestView.this, "网络传输错误", 5).show();
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