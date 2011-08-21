package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.server.user.UserStore;

import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.http.RequestToken;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class WeiboCheck extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_check);

        ImageButton post = (ImageButton) findViewById(R.id.btnPost);
        ImageButton beginOAuthBtn = (ImageButton) findViewById(R.id.Connect);

        if (!"".equals(UserStore.getAccessToken(this))) {
            ((TextView) findViewById(R.id.connect_info)).setText(R.string.weibo_re_binding);
            post.setVisibility(View.VISIBLE);
        }

        beginOAuthBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                bindWeibo(WeiboCheck.this);
            }
        });

        post.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(WeiboCheck.this, Post.class);
                startActivity(it);
                finish();
            }
        });
    }

    public static boolean bindWeibo(Context context) {
        Weibo weibo = OAuthConstant.getInstance().getWeibo();
        RequestToken requestToken;
        try {
            requestToken = weibo.getOAuthRequestToken("chinatvguide://OAuthActivity");
            String uri = requestToken.getAuthenticationURL() + "&from=xweibo";
            OAuthConstant.getInstance().setRequestToken(requestToken);

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("url", uri);
            intent.putExtras(bundle);
            intent.setClass(context, WebViewActivity.class);
            context.startActivity(intent);
            return true;
        } catch (WeiboException e) {
            e.printStackTrace();
            Log.w("bindWeibo",  e.getMessage());
            Toast.makeText(context, R.string.user_weibo_login_failed, Toast.LENGTH_LONG).show();
            return false;
        }
    }

}
