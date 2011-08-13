package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;

import weibo4android.Weibo;
import weibo4android.WeiboException;
import weibo4android.http.RequestToken;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class WeiboCheck extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weibo_check);
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
        ImageButton post = (ImageButton) findViewById(R.id.btnPost);
        ImageButton beginOAuthBtn = (ImageButton) findViewById(R.id.Connect);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!"".equals(sp.getString(OAuthActivity.ACCESS_TPKEN, ""))) {
            ((TextView) findViewById(R.id.connect_info)).setText(R.string.weibo_re_binding);
            post.setVisibility(View.VISIBLE);
        }

        beginOAuthBtn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                    intent.setClass(WeiboCheck.this, WebViewActivity.class);
                    startActivity(intent);
                } catch (WeiboException e) {
                    e.printStackTrace();
                }
            }
        });

        post.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(getApplicationContext(), Post.class);
                startActivity(it);
                finish();
            }
        });
    }

}
