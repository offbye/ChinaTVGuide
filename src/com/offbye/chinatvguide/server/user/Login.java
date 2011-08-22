
package com.offbye.chinatvguide.server.user;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.Validator;
import com.offbye.chinatvguide.weibo.WeiboCheck;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class Login extends Activity {
    protected static final String TAG = "Login";

    private Context mContext;

    private EditText email;

    private EditText password;

    private Button login;
    private ImageButton weiboLogin;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mContext = this;
        init();
    }

    private void init() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        weiboLogin = (ImageButton) findViewById(R.id.weiboLogin);
        login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!Validator.isEmail(email.getText().toString().trim())) {
                    Toast.makeText(mContext, R.string.invalid_email_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().trim().length() < 6) {
                    Toast.makeText(mContext, R.string.user_password_invalid, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                
                showProgressDialog();
                login();

            }

        });
        
        weiboLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
               WeiboCheck.bindWeibo(mContext);
            }
        });
    }

    private void showProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.msg_wait));
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
    }

    private void login() {
        new Thread() {
            public void run() {
                String url = String.format(Constants.URL_USER_LOGIN, email.getText().toString().trim(),
                        password.getText().toString().trim());
                try {
                    Log.d(TAG, "url:  " + url);
                    String resp = HttpUtil.getURL(url);
                    JSONObject respJson = new JSONObject(resp);
                    Log.d(TAG, "resp:  " + resp);
                    int status = respJson.getInt("status");
                    String msg = respJson.getString("msg");

                    mHandler.sendMessage(mHandler.obtainMessage(status, msg));
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(-5));
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendMessage(mHandler.obtainMessage(-5));
                }
            };
        }.start();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case -1:
                    if (null != pd) {
                        Toast.makeText(mContext, R.string.notify_network_error, Toast.LENGTH_SHORT)
                                .show();
                        pd.dismiss();
                    }
                    break;
                case 1:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(mContext, R.string.user_login_success, Toast.LENGTH_SHORT)
                                .show();
                    }
                    
                    UserStore.setEmail(mContext, email.getText().toString());
                    break;
                default:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(mContext, R.string.user_login_failed, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        };

    };

}
