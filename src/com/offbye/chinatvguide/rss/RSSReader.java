package com.offbye.chinatvguide.rss;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.util.HttpUtil;


public class RSSReader extends Activity 
{
	public String rssFeed = "http://ent.qq.com/tv/rss_tv.xml";
	private String rssTitle = "";
	public final String tag = "RSSReader";
	private RSSFeed feed = null;
	private RSSItem seleteditem = null;
	private ProgressDialog pd;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.rss);
        
        Bundle extras = getIntent().getExtras();
		if(extras!=null){
			rssTitle=extras.getString("title");
			rssFeed = extras.getString("url");
		}
		
		pd = ProgressDialog.show(this, getString(R.string.msg_loading), getString(R.string.msg_wait), true, false);
		pd.setIcon(R.drawable.icon);
		new GetFeed().start();
       // feed = getFeed(rssFeed);
        // display UI
        
    }
    
    private class GetFeed extends Thread{
    	public void run(){
    		feed = getFeed(rssFeed);
    		progressHandler.sendEmptyMessage(R.string.notify_succeeded);
    	}
    }
    private Handler progressHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case R.string.notify_succeeded:
				pd.dismiss();
				UpdateDisplay();
				break;
			case R.string.notify_service_exception:
				pd.dismiss();
				Toast.makeText(RSSReader.this,R.string.notify_service_exception, 5).show();
				break;
			default:
				Toast.makeText(RSSReader.this,R.string.notify_service_exception, 5).show();
			}
		}
	};
    
    private RSSFeed getFeed(String urlToRssFeed)
    {
    	RSSHandler theRssHandler = new RSSHandler();
    	try
    	{
           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           //RSSHandler theRssHandler = new RSSHandler();
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           // get our data via the url class
           
           InputSource is = null;
           if(HttpUtil.isCmwap(RSSReader.this)){
        	   is = new InputSource(HttpUtil.getXMLStreamByCmwap(urlToRssFeed));
           }
           else{
               // setup the url
        	   URL url = new URL(urlToRssFeed);
               is = new InputSource(url.openStream());
           }

           // perform the synchronous parse           
           xmlreader.parse(is);
           // get the results - should be a fully populated RSSFeed instance, or null on error
           
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    		Log.e(tag, ee.getMessage());
    		progressHandler.sendEmptyMessage(R.string.notify_service_exception);
    	}
    	return theRssHandler.getFeed();
    }

    private void UpdateDisplay()
    {
        TextView feedtitle = (TextView) findViewById(R.id.feedtitle);
        TextView feedpubdate = (TextView) findViewById(R.id.feedpubdate);
        ListView itemlist = (ListView) findViewById(R.id.itemlist);
        
        if (feed == null)
        {
        	feedtitle.setText("No RSS Feed Available");
        	return;
        }
        
        feedtitle.setText(rssTitle);
        feedpubdate.setText(feed.getPubDate());
        RSSAdapter adapter=new RSSAdapter(this,R.layout.rss_row,feed.getAllItems());
        itemlist.setAdapter(adapter);
        itemlist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long id)
			{
				if(arg1.findViewById(R.id.description).getVisibility()==View.GONE){
					arg1.findViewById(R.id.description).setVisibility(View.VISIBLE);
				}
				else{
					arg1.findViewById(R.id.description).setVisibility(View.GONE);
				}
			}
		});
        itemlist.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int position, long id)
			{
				seleteditem = feed.getAllItems().get(position);
				new AlertDialog.Builder(RSSReader.this)
                .setTitle(R.string.select_dialog)
                .setItems(R.array.rss, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       if(which==0){
                    	   Uri uri = Uri.parse(seleteditem.getLink());
                    	   Intent it  = new Intent(Intent.ACTION_VIEW,uri);
                    	   startActivity(it);
                       }
                       if(which==1){
//                    	   Intent i = new Intent(Intent.ACTION_VIEW);   
//                    	   i.putExtra("sms_body", seleteditem.getTitle()+"  \n"+seleteditem.getLink());   
//                    	   i.setType("vnd.android-dir/mms-sms");   
//                    	   startActivity(i);
                    	   Intent it = new Intent(Intent.ACTION_SEND);   
                    	   //it.putExtra(Intent.EXTRA_EMAIL, "me@abc.com");   
                    	   it.putExtra(Intent.EXTRA_TEXT, seleteditem.getTitle()+"  \n"+seleteditem.getLink());
                    	   it.setType("text/plain");
                    	   startActivity(Intent.createChooser(it, getString(R.string.msg_share)));  
                       }
                    }
                }).show();
				return true;
			}
		});
    }
       
}