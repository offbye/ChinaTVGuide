
package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentTask;
import com.offbye.chinatvguide.server.user.UserStore;

import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Post extends Activity {

    private final static String TAG = "Post";

    static Object lock = new Object();

    private TextView mCount;

    private EditText mContent;

    private Button mPost;
    private CheckBox isPostWeibo;
    private ProgressDialog pd;

    private String mChannel;

    private String mProgram;
    private String msg;

    private Context mContext;

    public static void addWeibo(Context context, TVProgram p) {
        Intent it = new Intent();
        if (null != p){
            it.putExtra("channel", p.getChannelname());
            it.putExtra("program", p.getProgram());
        }

        it.setClass(context, Post.class);
        context.startActivity(it);
    }

    public static Status post(Context context, String content) throws WeiboException {
        Status status = null;
        String token = UserStore.getAccessToken(context);
        String secret = UserStore.getAccessSecret(context);
        Weibo weibo = OAuthConstant.getInstance().getWeibo();
        OAuthConstant.getInstance().setToken(token);
        OAuthConstant.getInstance().setTokenSecret(secret);
        weibo.setToken(token, secret);

        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (null != location) {
            status = weibo.updateStatus(content, location.getLatitude(), location.getLongitude());
        } else {
            status = weibo.updateStatus(content);
        }
        Log.i("post", "status=" + status);
        return status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        mContext = getApplicationContext();
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);

        init();

        mChannel = getIntent().getStringExtra("channel");
        mProgram = getIntent().getStringExtra("program");
        
        msg = "";
        if (null != mChannel && !"".equals(mChannel) && null != mProgram && !"".equals(mProgram)){
            msg = "\n " + mContext.getString(R.string.comment) + " #"
            + mChannel.trim() + "#, #" + mProgram.trim() + "#";
            mContent.setHint(msg);
        }

    }

    private void init() {
        mCount = (TextView) findViewById(R.id.count);
        mContent = (EditText) findViewById(R.id.content);
        mPost = (Button) findViewById(R.id.post);
        isPostWeibo = (CheckBox) findViewById(R.id.isPostWeibo);
        if (!"".equals( UserStore.getAccessToken(mContext))){
            isPostWeibo.setChecked(true);
        }

        mContent.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable s) {
              
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCount.setText(mContext.getString(R.string.comment_have_input) + mContent.length() +"/140");
            }});
        mPost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mContent.getText().toString().trim().length() < 10){
                    Toast.makeText(mContext, R.string.comment_min_length, Toast.LENGTH_LONG).show();
                    return;
                }
                
                showProgressDialog();

                Comment c = new Comment();
                c.setChannel(mChannel);
                c.setProgram(mProgram);
                c.setContent(mContent.getText().toString());
                c.setType("1");
                c.setLocation(UserStore.getLocation(mContext));
                c.setScreenName(UserStore.getScreenName(mContext));
                if ("".equals(UserStore.getUserId(mContext))) {
                    c.setUserid("guest");
                } else {
                    c.setUserid(UserStore.getUserId(mContext));
                }
                c.setEmail(UserStore.getEmail(mContext));
             
                new CommentTask(getApplicationContext(), CommentTask.genUrl(c), mCallback).start();
                
                if (!"".equals( UserStore.getAccessToken(mContext)) && isPostWeibo.isChecked()){
                    
                    post(mContent.getText().toString() + msg);
                }
            }
        });
    }

    private CommentTask.Callback mCallback = new CommentTask.Callback() {

        @Override
        public void update(int status) {
            if (status < 0) {
                mHandler.sendMessage(mHandler.obtainMessage(-2, null));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(2, null));
            }
        }
    };

    private void showProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.msg_wait));
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
    }

    private void post(String content) {
        Weibo weibo = OAuthConstant.getInstance().getWeibo();
        String token = UserStore.getAccessToken(mContext);
        String secret = UserStore.getAccessSecret(mContext);
        OAuthConstant.getInstance().setToken(token);
        OAuthConstant.getInstance().setTokenSecret(secret);
        weibo.setToken(token, secret);

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            Status status;
            if (null != location) {
                status = weibo.updateStatus(content, location.getLatitude(), location
                        .getLongitude());
            } else {
                status = weibo.updateStatus(content);
            }
            Log.d("post", "status=" + status);
            mHandler.sendMessage(mHandler.obtainMessage(1, status));

        } catch (WeiboException e) {
            mHandler.sendMessage(mHandler.obtainMessage(-1, e));
            Log.e(TAG, e.getMessage());
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.weibo_post_ok, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case -1:
                    if (null != pd) {
                        Toast.makeText(Post.this, R.string.comment_weibo_failed,
                                Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    break;
                case 2:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.comment_succeed, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case -2:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.comment_failed, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        };
    };
}
