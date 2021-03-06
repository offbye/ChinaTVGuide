
package com.offbye.chinatvguide;

import com.offbye.chinatvguide.grid.Grid;
import com.offbye.chinatvguide.server.media.GetMediaListTask;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.LocationUtils;
import com.offbye.chinatvguide.weibo.OAuthConstant;

import org.json.JSONObject;

import weibo4android.Weibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChinaTVGuide extends Activity {
    private static final String TAG = "ChinaTVGuide";

    ImageView imageview;

    TextView textview;

    private Context mContext;

    private boolean isConnectOn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        mContext = this;
        imageview = (ImageView) this.findViewById(R.id.ImageView01);
        textview = (TextView) this.findViewById(R.id.TextView01);

        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            isConnectOn = true;
        }

        new Thread(new Runnable() {
            public void run() {
                initDb();
                if (isConnectOn) {
                    if(checkUpdate()){
                        return;
                    }
                }
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessageDelayed(msg, 1000);
            }
        }).start();

        if (isConnectOn && 5 < Integer.valueOf(Build.VERSION.SDK)) {
            new Thread() {
                public void run() {
                    try {

                        LocationUtils.getLocation(getApplicationContext());
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                };
            }.start();
        }

        final String token = UserStore.getAccessToken(mContext);
        final String secret = UserStore.getAccessSecret(mContext);
        if (isConnectOn && token.length() > 0 && secret.length() > 0) {
            new Thread() {
                public void run() {
                    try {
                        final Weibo weibo = OAuthConstant.getInstance().getWeibo();
                        OAuthConstant.getInstance().setToken(token);
                        OAuthConstant.getInstance().setTokenSecret(secret);
                        weibo.setToken(token, secret);
                        weibo4android.User u = weibo.verifyCredentials();
                        if (null != u) {
                            UserStore.setScreenName(mContext, u.getScreenName());
                        }
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                };
            }.start();
        }
        
        //get media list.
        if (isConnectOn) {
            new GetMediaListTask(mContext).execute();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                if (isConnectOn){
                    syncPerDay();
                }
                enterHome();
            } else if (msg.what == 3) {
                showUpdate((String[]) msg.obj);
            }

        }

    };

    private void syncPerDay() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        if (PreferencesActivity.isAutoSyncOn(ChinaTVGuide.this)
                && PreferencesActivity.getSyncToday(ChinaTVGuide.this).compareTo(df.format(new Date())) != 0) {
            Log.d(TAG, "we start SyncService");
            startService(new Intent(ChinaTVGuide.this, SyncService.class));
        }
    }

    private void enterHome() {
        Intent i = new Intent(this, Grid.class);
        startActivity(i);
        finish();
    }

    private void initDb() {
        MydbHelper mydb = new MydbHelper(this);
        mydb.FirstStart();
        mydb.close();
    }

    private void showUpdate(final String[] msg) {
        new AlertDialog.Builder(mContext).setIcon(R.drawable.icon).setTitle(
                R.string.notify_newversion).setMessage(msg[0]).setPositiveButton(
                R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(msg[1]));
                        startActivity(i);
                    }
                }).setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        enterHome();
                    }
                }).show();
    }

    private boolean checkUpdate() {
        boolean needUpdate = false;
        try {
            String sb = HttpUtil.getUrl(mContext, Constants.URL_UPDATE);
            if (sb.contains("versionCode")) {
                JSONObject st = new JSONObject(sb.toString());
                Constants.setUrlTvs(mContext, st.getString(Constants.URL_TVS));
                Constants.setUrlBroadcast(mContext, st.getString(Constants.URL_BROADCAST));
                Constants.setUrlShuzi(mContext, st.getString(Constants.URL_SHUZI));
                Constants.setUrlSync(mContext, st.getString(Constants.URL_SYNC));
                Constants.setUrlLocal(mContext, st.getString(Constants.URL_LOCAL));

                int versionCode = st.getInt("versionCode");
                String versionUrl = st.getString("versionUrl");
                String versionInfo = st.getString("versionInfo");

                if (Constants.VERSION_CODE < versionCode) {
                    Message m = mHandler.obtainMessage();
                    m.what = 3;
                    m.obj = new String[] {
                            versionInfo, versionUrl
                    };
                    mHandler.sendMessage(m);
                    needUpdate = true;
                }
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return needUpdate;
    }
}
