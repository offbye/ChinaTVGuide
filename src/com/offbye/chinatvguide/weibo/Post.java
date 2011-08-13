package com.offbye.chinatvguide.weibo;


import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentTask;

import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Post extends Activity {

	private final static String TAG = "Post";

	static Object lock = new Object();
	private TextView mCount;
	private EditText mContent;
	private ImageButton mPost;
	private ProgressDialog pd;
	private String token;
	private String secret;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
		System.setProperty("weibo4j.oauth.consumerSecret",
				Weibo.CONSUMER_SECRET);

		init();
		String msg = this.getIntent().getStringExtra("msg");
		
		if (!"".equals(msg)) {
			mContent.setText(msg);
		}
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (!"".equals(sp.getString(OAuthActivity.ACCESS_TPKEN, ""))) {
			token = sp.getString(OAuthActivity.ACCESS_TPKEN, "");
			secret = sp.getString(OAuthActivity.ACCESS_TPKEN_SECRET, "");
		} else {
			Intent it = new Intent();
			it.setClass(getApplicationContext(), WeiboCheck.class);
			this.startActivity(it);
			finish();
		}
	}

	private void init() {
		mCount = (TextView) findViewById(R.id.count);
		mContent = (EditText) findViewById(R.id.content);
		mPost = (ImageButton) findViewById(R.id.post);
		mPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgressDialog();
				//post(mContent.getText().toString());
				
				Comment c = new Comment();
				c.userid = "u";
				c.channel = "ccc";
				c.program = "p";
				c.screenName = "s";
				
				new CommentTask(getApplicationContext(),CommentTask.genUrl(c),mCallback).start();
			}
		});
	}
	private CommentTask.Callback mCallback = new CommentTask.Callback(){

        @Override
        public void update(Object message) {
            if(message instanceof Exception)
            {
                mHandler.sendMessage(mHandler.obtainMessage(1, null));
            }
            else {
                mHandler.sendMessage(mHandler.obtainMessage(0, null));
            }
            
           
            
        }};
	

	private void showProgressDialog() {
		pd = new ProgressDialog(this);
		pd.setMessage(getString(R.string.msg_wait));
		pd.setCancelable(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
	}

	private void post(String content) {
		Weibo weibo = OAuthConstant.getInstance().getWeibo();
		OAuthConstant.getInstance().setToken(token);
		OAuthConstant.getInstance().setTokenSecret(secret);
		weibo.setToken(token, secret);

		try {
			LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			Location location = locationManager
					.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

			Status status;
			if (null != location) {
				status = weibo.updateStatus(content, location.getLatitude(),
						location.getLongitude());
			} else {
				status = weibo.updateStatus(content);
			}
			Log.i("post", "status=" + status);
			mHandler.sendMessage(mHandler.obtainMessage(0, status));

		} catch (WeiboException e) {
			mHandler.sendMessage(mHandler.obtainMessage(1, e));
			e.printStackTrace();
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (null != pd) {
					pd.dismiss();
					Toast.makeText(Post.this, R.string.weibo_post_ok,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 1:
				if (null != pd) {
					Toast.makeText(Post.this,
							((WeiboException) msg.obj).getMessage(),
							Toast.LENGTH_SHORT).show();
					pd.dismiss();
				}
				break;
			}
		};

	};

}
