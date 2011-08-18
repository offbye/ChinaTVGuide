
package com.offbye.chinatvguide.server.user;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.weibo.OAuthActivity;
import com.offbye.chinatvguide.weibo.Post;

import org.json.JSONException;
import org.json.JSONObject;

import weibo4android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class UserInfoActivity extends Activity {
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

        String token = OAuthActivity.getAccessToken(mContext);
        String secret = OAuthActivity.getAccessToken(mContext);
        if (!"".equals(OAuthActivity.getAccessSecret(mContext))) {

            // Weibo weibo = OAuthConstant.getInstance().getWeibo();
            // OAuthConstant.getInstance().setToken(token);
            // OAuthConstant.getInstance().setTokenSecret(secret);
            // weibo.setToken(token, secret);
            // try {
            // weibo.getAuthenticatedUser();
            // } catch (WeiboException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(mContext, R.string.weibo_post_ok, Toast.LENGTH_SHORT).show();
                    }
                    break;
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
                String url = String.format(Constants.URL_USERINFO, "user");
                try {
                    String status = HttpUtil.getURL(url);
                    JSONObject user = new JSONObject(status);
                    Log.v(TAG, status);
                    String exist = user.getString("exist");
                    Log.v(TAG, "" + user.getString("point"));
                    Log.v(TAG, "" + user.getString("comment"));
                    
                    
                    User u = new User();
                    mHandler.sendMessage(mHandler.obtainMessage(0, status));
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(-1));
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(-1));
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

        mScreenNameTv.setText(getScreenName(mContext));
        mEmailTv.setText(getEmail(mContext));
        mLocationTv.setText(getLocation(mContext));
        mPointTv.setText(getPoint(mContext));
        mCheckinTv.setText(getCheckin(mContext));
        mCommentTv.setText(getComment(mContext));
    }

    public static String getScreenName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        return sp.getString(OAuthActivity.SCREEN_NAME, "");
    }

    public static String getEmail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        return sp.getString(EMAIL, "");
    }

    public static String getLocation(Context context) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        return sp.getString(LOCATION, "");
    }

    public static String getPoint(Context context) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        return sp.getString(POINT, "");
    }

    public static String getCheckin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        return sp.getString(CHECKIN, "");
    }

    public static String getComment(Context context) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        return sp.getString(COMMENT, "");
    }
    
    
    public static void setPoint(Context context,String point) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
         sp.edit().putString(POINT, point).commit();
    }

    public static void setCheckin(Context context,String checkin) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        sp.edit().putString(CHECKIN, checkin).commit();
    }

    public static void setComment(Context context,String comment) {
        SharedPreferences sp = context.getSharedPreferences(OAuthActivity.PREFS_USER, 0);
        sp.edit().putString(COMMENT, comment).commit();
    }

}
