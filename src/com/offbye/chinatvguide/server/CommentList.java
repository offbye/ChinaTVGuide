
package com.offbye.chinatvguide.server;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.server.PullToRefreshListView.OnFooterClickListener;
import com.offbye.chinatvguide.server.PullToRefreshListView.OnRefreshListener;
import com.offbye.chinatvguide.server.user.Login;
import com.offbye.chinatvguide.server.user.UserInfoActivity;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.weibo.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;

public class CommentList extends ListActivity {

    private static final String TAG = "CommentList";

    private LinkedList<Comment> pl = new LinkedList<Comment>();

    private ProgressDialog pd;

    private String servermsg;

    private StringBuffer urlsb;

    private Context mContext;
    private volatile long lastestId;
    private volatile long oldestId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pull_to_refresh);
       
        mContext = this;

        urlsb = new StringBuffer();
        urlsb.append(Constants.URL_COMMENT_LIST);
        if ("1".equals(this.getIntent().getStringExtra("type"))) {
            urlsb.append("?type=1");
        } else {
            urlsb.append("?type=0");
        }
        
        //if login or regiser, email is dame with userid.
        if (null != this.getIntent().getStringExtra("userid")
                && !"".equals(this.getIntent().getStringExtra("userid"))) {
            urlsb.append("&userid=").append(this.getIntent().getStringExtra("userid"));
        }
        if (null != this.getIntent().getStringExtra("program")
                && !"".equals(this.getIntent().getStringExtra("program"))) {
            setTitle(this.getIntent().getStringExtra("program"));
            urlsb.append("&program=").append(URLEncoder.encode(this.getIntent().getStringExtra("program")));
        }
        Log.d(TAG, urlsb.toString());
        getData();
        
        ((PullToRefreshListView) getListView())
                .setOnFooterClickListener(new OnFooterClickListener() {

                    @Override
                    public void update() {
                        Log.d(TAG, "footer clicked");
                        new GetMoreDataTask().execute();

                    }
                });
 
        

        // Set a listener to be invoked when the list should be refreshed.
        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetNewDataTask().execute();
            }
        });
    }
    private class GetMoreDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            try {
                oldestId = (null != pl.getLast()) ? pl.getLast().getId() : 0;
                if (oldestId > 0) {
                    String url = urlsb.toString();
                    url += "&action=old&id=" + oldestId;
                    Log.d(TAG, "get old " + url);
                    ArrayList<Comment> list = getComment(url);
                    Log.d(TAG, "get old " + list.size());
                    pl.addAll(list);
                }
            } catch (Exception e) {
                ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //mListItems.addFirst("Added after refresh...");

            // Call onRefreshComplete when the list has been refreshed.
            ((PullToRefreshListView) getListView()).onFooterRefreshComplete();

            super.onPostExecute(result);
        }
    }
    
    private class GetNewDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                lastestId = (null != pl.getFirst()) ? pl.getFirst().getId() : 0;
                if (lastestId > 0){
                    String url = urlsb.toString();
                    url += "&action=new&id=" + lastestId;
                    Log.d(TAG, "get new " + url);
                    ArrayList<Comment> list  = getComment(url);
                    Log.d(TAG, "get new " + list.size());
                    pl.addAll(0, list);
                }
            } catch (Exception e) {
                ;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            //mListItems.addFirst("Added after refresh...");

            // Call onRefreshComplete when the list has been refreshed.
            ((PullToRefreshListView) getListView()).onRefreshComplete();

            super.onPostExecute(result);
        }
    }
    
    public ArrayList<Comment> getComment(String weburl) {
        ArrayList<Comment> pl = new ArrayList<Comment>();
        try {
            String sb = HttpUtil.getUrl(this, weburl);
            if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
                JSONArray ja = new JSONArray(sb.toString());
                int length = ja.length();
                
                for (int i = 0; i < length; i++) {
                    JSONObject row = ja.getJSONObject(i);
                    // Log.d(TAG, "jp.toString()" + row.toString());
                    Comment c = new Comment();
                    c.setId(row.getLong("id"));
                    c.setContent(row.getString("content"));
                    c.setChannel(row.getString("channel"));
                    c.setProgram(row.getString("program"));
                    c.setTime(row.getString("createtime"));
                    c.setScreenName(row.getString("screenname"));
                    c.setUserid(row.getString("userid"));
                    c.setType(row.getString("type"));
                    c.setLocation(row.getString("location"));
                    pl.add(c);
                    
                }
            }

        } catch (IOException e) {
            mHandler.sendEmptyMessage(R.string.notify_network_error);
        } catch (JSONException e) {
            mHandler.sendEmptyMessage(R.string.notify_json_error);
        } catch (AppException e) {
            mHandler.sendEmptyMessage(R.string.notify_no_connection);
        }
        return pl;
    }

    public void getData() {
        pd = ProgressDialog.show(this, getString(R.string.msg_loading),
                getString(R.string.msg_wait), true, false);
        pd.setIcon(R.drawable.icon);
        new Thread(new GetDataBody()).start();
    }

    private class GetDataBody implements Runnable {
        public void run() {
            pl.addAll(getComment(urlsb.toString()));
            if (pl.size() > 0) {
                mHandler.sendEmptyMessage(R.string.notify_succeeded);
            } else {
                mHandler.sendEmptyMessage(R.string.notify_no_result);
            }
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.string.notify_succeeded:
                    pd.dismiss();
                    CommentAdapter pa = new CommentAdapter(mContext, R.layout.comment_item, pl);
                    ((PullToRefreshListView) getListView()).setAdapter(pa);
                    ((PullToRefreshListView) getListView()).setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long id) {

                        }

                    });

                    break;
                case R.string.notify_network_error:
                    pd.dismiss();
                    Toast.makeText(mContext, R.string.notify_network_error, 5).show();
                    break;
                case R.string.notify_json_error:
                    pd.dismiss();
                    Toast.makeText(mContext, R.string.notify_json_error, 5).show();
                    break;
                case R.string.notify_database_error:
                    pd.dismiss();
                    Toast.makeText(mContext, R.string.notify_database_error, 5).show();
                    break;
                case R.string.notify_no_result:
                    pd.dismiss();
                    Toast.makeText(mContext, R.string.notify_no_result, 5).show();
                    break;
                case R.string.notify_no_connection:
                    pd.dismiss();
                    Toast.makeText(mContext, R.string.notify_no_connection, 5).show();
                    break;
                case R.string.notify_newversion:
                    pd.dismiss();

                    Toast.makeText(mContext, servermsg.split("--")[4], 5).show();

                    new AlertDialog.Builder(mContext).setIcon(R.drawable.icon).setTitle(
                            servermsg.split("--")[3]).setMessage(servermsg.split("--")[4])
                            .setPositiveButton(R.string.alert_dialog_ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            Intent i = new Intent(Intent.ACTION_VIEW, Uri
                                                    .parse(servermsg.split("--")[2]));
                                            startActivity(i);
                                        }
                                    }).show();

                    break;
                default:
                    Toast.makeText(mContext, R.string.notify_no_result, 5).show();
            }
        }
    };
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int userlabel = ("".equals(UserStore.getUserId(mContext))) ?R.string.user_login:R.string.user_info;
        menu.add(0, 0, 0,  this.getText(userlabel)).setIcon(R.drawable.ic_menu_user);
        menu.add(0, 1, 1,  this.getText(R.string.comment_post)).setIcon(R.drawable.ic_menu_edit);
               
//        if (10 < Integer.valueOf(Build.VERSION.SDK)) {
//            menu.getItem(0).setShowAsAction(1);
//            menu.getItem(1).setShowAsAction(1);
//        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            if ("".equals(UserStore.getUserId(this)) && "".equals(UserStore.getEmail(this))) {
                Intent it2 = new Intent();
                it2.setClass(this, Login.class);
                this.startActivity(it2);
            }
            else {
                Intent it2 = new Intent();
                it2.setClass(this, UserInfoActivity.class);
                this.startActivity(it2);
            }
            break;

        case 1:
            Intent i = new Intent();
            i.setClass(mContext, Post.class);
            startActivity(i);
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
