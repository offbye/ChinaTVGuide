
package com.offbye.chinatvguide.server.user;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.Validator;

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
import android.widget.Toast;

import java.io.IOException;

public class Register extends Activity {
    protected static final String TAG = "Register";

    private Context mContext;

    private EditText screenName;

    private EditText email;

    private EditText password;

    private EditText passwordAgain;

    private Button register;

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mContext = this;
        init();
    }

    private void init() {
        screenName = (EditText) findViewById(R.id.screenname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        passwordAgain = (EditText) findViewById(R.id.passwordAgain);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (screenName.getText().toString().trim().length() < 2) {
                    Toast.makeText(mContext, R.string.user_screenname_invalid, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (!Validator.isEmail(email.getText().toString().trim())) {
                    Toast.makeText(mContext, R.string.invalid_email_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().trim().length() < 6) {
                    Toast.makeText(mContext, R.string.user_password_invalid, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (!password.getText().toString().trim().equals(passwordAgain.getText().toString().trim())) {
                    Toast.makeText(mContext, R.string.user_password_not_same, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                showProgressDialog();
                regiter();

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

    private void regiter() {
        new Thread() {
            public void run() {
                String url = String.format(Constants.URL_USER_REG, email.getText().toString().trim(),
                        password.getText().toString().trim(), screenName.getText().toString().trim());
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
                case -5:
                    if (null != pd) {
                        Toast.makeText(mContext, R.string.notify_network_error, Toast.LENGTH_LONG)
                                .show();
                        pd.dismiss();
                    }
                    break;
                case 1:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(mContext, R.string.user_register_success, Toast.LENGTH_LONG)
                                .show();
                        UserStore.setScreenName(mContext, screenName.getText().toString());
                        UserStore.setEmail(mContext, email.getText().toString());
                        finish();
                    }

                    break;
                default:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(mContext, R.string.user_register_failed, Toast.LENGTH_LONG)
                                .show();
                    }
                    break;
            }
        };

    };

}
