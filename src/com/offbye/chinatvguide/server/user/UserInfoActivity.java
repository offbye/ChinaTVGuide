
package com.offbye.chinatvguide.server.user;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.server.CommentList;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.weibo.WeiboCheck;

import org.json.JSONException;
import org.json.JSONObject;

import weibo4android.WeiboException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class UserInfoActivity extends Activity {

    protected static final String TAG = "UserInfoActivity";

    private Context mContext;

    private TextView mScreenNameTv;

    private String mEmail;
    private String mUserid;
    private TextView mEmailTv;

    private TextView mLocationTv;

    private TextView mPointTv;

    private TextView mCheckinTv;

    private TextView mCommentTv;
    private RelativeLayout layoutCheckin;
    private RelativeLayout layoutComment;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        mContext = this;

       
        mEmail = UserStore.getEmail(mContext);
        mUserid = UserStore.getUserId(mContext);
        init();
        
        if ("".equals(mUserid) && "".equals(mEmail)) {
            new AlertDialog.Builder(this).setIcon(R.drawable.icon).setTitle(R.string.user_login)
                    .setMessage(R.string.user_guest_tip).setPositiveButton(R.string.user_login,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent it = new Intent();
                                    it.setClass(mContext, Login.class);
                                    startActivity(it);
                                    finish();
                                }
                            }).setNeutralButton(R.string.user_reg,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent it = new Intent();
                                    it.setClass(mContext, Register.class);
                                    startActivity(it);
                                    finish();
                                }
                            }).setNegativeButton(R.string.weibo_menu,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    WeiboCheck.bindWeibo(mContext);
                                }
                            }).show();
            return;
        } else {
            showProgressDialog();
            getUserInfo();
        }

        final String token = UserStore.getAccessToken(mContext);
        final String secret = UserStore.getAccessToken(mContext);
        if (!"".equals(UserStore.getAccessSecret(mContext))) {
            /*
             * new Thread() { public void run() { try { final Weibo weibo =
             * OAuthConstant.getInstance().getWeibo();
             * OAuthConstant.getInstance().setToken(token);
             * OAuthConstant.getInstance().setTokenSecret(secret);
             * weibo.setToken(token, secret); weibo4android.User u =
             * weibo.getAuthenticatedUser(); if (null!= u){ Log.d(TAG,
             * u.toString()); SharedPreferences sp =
             * mContext.getSharedPreferences(PREFS_USER, 0);
             * sp.edit().putString(SCREEN_NAME, u.getScreenName()).commit();
             * sp.edit().putString(LOCATION,u.getLocation()).commit();
             * sp.edit().putString(SCREEN_NAME, u.getScreenName()).commit(); }
             * mHandler.sendMessage(mHandler.obtainMessage(3)); } catch
             * (Exception e) { // TODO Auto-generated catch block
             * e.printStackTrace(); } }; }.start();
             */
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case -1:
                    if (null != pd) {
                        Toast.makeText(mContext, ((WeiboException) msg.obj).getLocalizedMessage(),
                                Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    break;
                case 2:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(mContext, R.string.comment_succeed, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case 3:
                    if (null != pd) {
                        pd.dismiss();
                    }
                    Toast.makeText(mContext, R.string.user_get_success, Toast.LENGTH_SHORT).show();
                    setvalue();
                    break;
                case -3:
                    if (null != pd) {
                        pd.dismiss();
                    }
                    Toast.makeText(mContext, R.string.user_get_fail, Toast.LENGTH_SHORT).show();
                    break;
            }
        };

    };

    private void showProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.msg_wait));
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
    }

    private void getUserInfo() {
        new Thread() {
            public void run() {
               
                if ("".equals(mUserid) && "".equals(mEmail)) {
                    return;
                }
                String url = Constants.URL_USERINFO;
                if (!"".equals(mUserid)) {
                    url = url + "?userid=" + mUserid;
                } else {
                    url = url + "?email=" + mEmail;
                }
                
                try {
                    Log.d(TAG, "url:  " + url);
                    String status = HttpUtil.getURL(url);
                    JSONObject user = new JSONObject(status);
                    Log.d(TAG, "status:  " + status);
                    boolean exist = user.getBoolean("exist");
                    if (exist) {
                        UserStore.setPoint(mContext, user.getInt("point"));
                        UserStore.setComment(mContext, user.getInt("comment"));
                        UserStore.setCheckin(mContext, user.getInt("checkin"));
                    }
                    mHandler.sendMessage(mHandler.obtainMessage(3, status));
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(-3));
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(-3));
                }
            };
        }.start();
    }

    private void init() {
        mScreenNameTv = (TextView) findViewById(R.id.screenname);
        mEmailTv = (TextView) findViewById(R.id.email);
        mLocationTv = (TextView) findViewById(R.id.location);
        mPointTv = (TextView) findViewById(R.id.point);
        mCheckinTv = (TextView) findViewById(R.id.checkin);
        mCommentTv = (TextView) findViewById(R.id.comment);

        Button refresh = (Button) findViewById(R.id.refresh);
        refresh.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgressDialog();
                getUserInfo();
            }
        });
        
        layoutCheckin = (RelativeLayout)findViewById(R.id.layoutCheckin);
        layoutComment = (RelativeLayout)findViewById(R.id.layoutComment);
        layoutCheckin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CommentList.class);
                i.putExtra("type", "0");
                i.putExtra("userid", mUserid);
                startActivity(i);
            }
        });

        layoutComment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CommentList.class);
                i.putExtra("type", "1");
                i.putExtra("userid", mUserid);
                startActivity(i);
            }
        });

    }

    private void setvalue() {
        String sname = UserStore.getScreenName(mContext);
        mScreenNameTv.setText("".equals(sname) ? mContext.getString(R.string.user_guest) : sname);
        mEmailTv.setText("" + UserStore.getEmail(mContext));
        mLocationTv.setText("" + UserStore.getLocation(mContext));
        mPointTv.setText("" + UserStore.getPoint(mContext) + mContext.getString(R.string.user_point));
        mCheckinTv.setText("" + UserStore.getCheckin(mContext));
        mCommentTv.setText("" + UserStore.getComment(mContext));
    }

}
