
package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;

import weibo4android.WeiboException;
import weibo4android.http.AccessToken;
import weibo4android.http.RequestToken;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class OAuthActivity extends Activity {
    public static final String ACCESS_TPKEN = "AccessToken";

    public static final String ACCESS_TPKEN_SECRET = "AccessTokenSecret";

    public static final String USERID = "userid";

    public static final String SCREEN_NAME = "screenname";

    private static final String TAG = "OAuthActivity";

    public static final String PREFS_USER = "user";

    ImageButton post;

    static Object lock = new Object();

    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
        TextView textView = (TextView) findViewById(R.id.TextView01);

        Uri uri = this.getIntent().getData();
        String verifer = uri.getQueryParameter("oauth_verifier");
        Log.d(TAG, verifer);
        try {
            RequestToken requestToken = OAuthConstant.getInstance().getRequestToken();
            if (null != requestToken) {
                AccessToken accessToken = requestToken.getAccessToken(verifer);
                OAuthConstant.getInstance().setAccessToken(accessToken);
                Log.d(TAG, "token:" + accessToken.getToken() + " key:"
                        + accessToken.getTokenSecret());
                textView.setText(R.string.weibo_connect_ok);
                SharedPreferences sp = getSharedPreferences(PREFS_USER, 0);

                sp.edit().putString(ACCESS_TPKEN, accessToken.getToken()).commit();
                sp.edit().putString(ACCESS_TPKEN_SECRET, accessToken.getTokenSecret()).commit();
                sp.edit().putString(USERID, "" + accessToken.getUserId()).commit();
                sp.edit().putString(SCREEN_NAME, accessToken.getScreenName()).commit();
            } else {
                textView.setText("connect failed");
                Log.d(TAG,"connect failed");
            }

        } catch (WeiboException e) {
            e.printStackTrace();
        }

        post = (ImageButton) findViewById(R.id.btnPost);
        post.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getApplicationContext(), Post.class);
                startActivity(it);
                finish();
            }
        });

    }

    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(OAuthActivity.USERID, "");
    }

    public static String getAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(OAuthActivity.ACCESS_TPKEN, "");
    }

    public static String getAccessSecret(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(OAuthActivity.ACCESS_TPKEN_SECRET, "");
    }

    public static String getScreenName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(OAuthActivity.SCREEN_NAME, "");
    }
}
