
package com.offbye.chinatvguide.server.user;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.weibo.OAuthActivity;
import com.offbye.chinatvguide.weibo.OAuthConstant;

import org.json.JSONException;
import org.json.JSONObject;

import weibo4android.Weibo;
import weibo4android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class UserInfoActivity extends Activity {
    public static final String PREFS_USER = "user";

    public static final String ACCESS_TPKEN = "AccessToken";

    public static final String ACCESS_TPKEN_SECRET = "AccessTokenSecret";

    public static final String USERID = "userid";

    public static final String SCREEN_NAME = "screenname";
    
    private static final String EMAIL = "email";

    private static final String POINT = "point";

    private static final String CHECKIN = "checkin";

    private static final String COMMENT = "comment";

    private static final String LOCATION = "location";

    protected static final String TAG = "UserInfoActivity";

    private Context mContext;

    private TextView mScreenNameTv;

    private String mEmail;

    private TextView mEmailTv;

    private TextView mLocationTv;

    private TextView mPointTv;

    private TextView mCheckinTv;

    private TextView mCommentTv;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        mContext = this;

        init();
        mEmail = getEmail(mContext);

        showProgressDialog();
        getUserInfo();

        final String token = getAccessToken(mContext);
        final String secret = getAccessToken(mContext);
        if (!"".equals(getAccessSecret(mContext))) {
            /*
             * new Thread() { public void run() { try { final Weibo weibo =
             * OAuthConstant.getInstance().getWeibo();
             * OAuthConstant.getInstance().setToken(token);
             * OAuthConstant.getInstance().setTokenSecret(secret);
             * weibo.setToken(token, secret); weibo4android.User u =
             * weibo.getAuthenticatedUser(); if (null!= u){ Log.d(TAG,
             * u.toString()); SharedPreferences sp =
             * mContext.getSharedPreferences(PREFS_USER, 0);
             * sp.edit().putString(SCREEN_NAME,
             * u.getScreenName()).commit();
             * sp.edit().putString(LOCATION,u.getLocation()).commit();
             * sp.edit().putString(SCREEN_NAME,
             * u.getScreenName()).commit(); }
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
                String userid = getUserId(mContext);
                if ("".equals(userid)) {
                    return;
                }
                String url = String.format(Constants.URL_USERINFO, userid);
                try {
                    Log.d(TAG, "url:  " + url);
                    String status = HttpUtil.getURL(url);
                    JSONObject user = new JSONObject(status);
                    Log.d(TAG, "status:  " + status);
                    boolean exist = user.getBoolean("exist");
                    if (exist) {
                        setPoint(mContext, user.getInt("point"));
                        setComment(mContext, user.getInt("comment"));
                        setCheckin(mContext, user.getInt("checkin"));
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

    }

    private void setvalue() {
        String sname = getScreenName(mContext);
        mScreenNameTv.setText("".equals(sname) ? mContext.getString(R.string.user_guest) : sname);
        mEmailTv.setText("" + getEmail(mContext));
        mLocationTv.setText("" + getLocation(mContext));
        mPointTv.setText("" + getPoint(mContext) + " points");
        mCheckinTv.setText("" + getCheckin(mContext));
        mCommentTv.setText("" + getComment(mContext));
    }
    

    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(USERID, "");
    }

    public static String getAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(ACCESS_TPKEN, "");
    }

    public static String getAccessSecret(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(ACCESS_TPKEN_SECRET, "");
    }


    public static String getScreenName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(SCREEN_NAME, "");
    }

    public static String getEmail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(EMAIL, "");
    }

    public static String getLocation(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(LOCATION, "");
    }

    public static int getPoint(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getInt(POINT, 0);
    }

    public static int getCheckin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getInt(CHECKIN, 0);
    }

    public static int getComment(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getInt(COMMENT, 0);
    }

    public static void setLocation(Context context, String location) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putString(LOCATION, location).commit();
    }

    public static void setPoint(Context context, int point) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putInt(POINT, point).commit();
    }

    public static void setCheckin(Context context, int num) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putInt(CHECKIN, num).commit();
    }

    public static void setComment(Context context, int num) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putInt(COMMENT, num).commit();
    }

}
