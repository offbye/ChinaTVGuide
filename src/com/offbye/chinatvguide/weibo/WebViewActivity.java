package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class WebViewActivity extends Activity 
{
	private WebView webView;
	private Intent intent = null;
	private Context mContext;
	public static WebViewActivity webInstance = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.web);
		setTitle(R.string.app_name);
		
		webInstance = this;
		mContext = getApplicationContext();
		webView  = (WebView)findViewById(R.id.web);  
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode( WebSettings.LOAD_NO_CACHE );
        
        webView.setOnTouchListener(new OnTouchListener()
        {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				webView.requestFocus();
				return false;
			}
        });
        
		intent = this.getIntent();
		if(!intent.equals(null))
		{
			Bundle b=intent.getExtras();
		    if(b!=null&&b.containsKey("url"))
		    {  
		    	webView.loadUrl(b.getString("url"));
		    	webView.setWebChromeClient(new WebChromeClient() {            
		    		  public void onProgressChanged(WebView view, int progress)               
		    		  {                   
		    			  setTitle(getString(R.string.msg_wait) + progress + "%");
		    			  setProgress(progress * 100);

		    			  if (progress == 100)	setTitle(R.string.app_name);
		    		  }
		    	});
		    }
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
	}
	
    /**
     * 监听BACK键
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {	
		if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 )
		{
			//SplashActivity.webInstance.finish();
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}