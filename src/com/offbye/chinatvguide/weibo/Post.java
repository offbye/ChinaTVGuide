
package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentTask;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.DES;
import com.offbye.chinatvguide.util.FileUtil;
import com.offbye.chinatvguide.util.HttpUtil;
import com.offbye.chinatvguide.util.ShakeDetector;
import com.offbye.chinatvguide.util.ShakeDetector.OnShakeListener;

import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Post extends Activity {

    private final static String TAG = "Post";
    private static final String IMG_FILE_PATH = "/sdcard/chinatvguide/";

    private static final String IMAGE_URL = "http://m.intotv.net/sc/s.php?m=";
    protected static final int MIN_INPUT = 5;

    private TextView mCount;

    private EditText mContent;

    private Button mPostButton;
    private Button mBackButton;
    private CheckBox isPostWeibo;
    private ImageView mImage;
    private ProgressDialog pd;
    private String mChannel;
    private String mChannelName;

    private String mProgram;
    private String msg;

    private Context mContext;
    
    private volatile File mFile;
    private volatile Bitmap mBitmap;
    private ShakeDetector mShakeDetector;

    public static void addWeibo(Context context, TVProgram p) {
        Intent it = new Intent();
        if (null != p){
            it.putExtra("channel", p.getChannel());
            it.putExtra("channelname", p.getChannelname());
            if (null !=  p.getProgram()) {
                it.putExtra("program", p.getProgram());
            }
        }

        it.setClass(context, Post.class);
        context.startActivity(it);
    }

    public static Status post(Context context, String content) throws WeiboException {
        Status status = null;
        String token = UserStore.getAccessToken(context);
        String secret = UserStore.getAccessSecret(context);
        Weibo weibo = OAuthConstant.getInstance().getWeibo();
        OAuthConstant.getInstance().setToken(token);
        OAuthConstant.getInstance().setTokenSecret(secret);
        weibo.setToken(token, secret);

        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (null != location) {
            status = weibo.updateStatus(content, location.getLatitude(), location.getLongitude());
        } else {
            status = weibo.updateStatus(content);
        }
        Log.i("post", "status=" + status);
        return status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post);
        mContext = getApplicationContext();

        
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);

        init();
        mChannel = getIntent().getStringExtra("channel");
        mChannelName = getIntent().getStringExtra("channelname");
        mProgram = getIntent().getStringExtra("program");
        
        msg = "";
        if (null != mChannelName && !"".equals(mChannelName) && null != mProgram && !"".equals(mProgram)){
            msg =  mContext.getString(R.string.comment) + " #"
            + mChannelName.trim() + "#, #" + mProgram.trim() + "#";
            mContent.setHint(msg);
        }
        else if (null != mChannelName && !"".equals(mChannelName)){
            msg =   mContext.getString(R.string.comment) + " #"
            + mChannelName.trim() + "#";
            mContent.setHint(msg);
        }
        msg = "\n " + msg;
        
        mShakeDetector = new ShakeDetector(mContext);
        mShakeDetector.registerOnShakeListener(new OnShakeListener(){

            @Override
            public void onShake() {
               doFetchImage();
            }});
        
        if (getIntent().getBooleanExtra("fetchImage", false)) {
            doFetchImage();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mShakeDetector.start();
    }

    @Override
    protected void onPause() {
        mShakeDetector.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (null != mBitmap)
            mBitmap.recycle();
        super.onDestroy();
    }

    private void init() {
        mCount = (TextView) findViewById(R.id.count);
        mContent = (EditText) findViewById(R.id.content);
        mPostButton = (Button) findViewById(R.id.right_button);
        mBackButton = (Button) findViewById(R.id.left_button);
        isPostWeibo = (CheckBox) findViewById(R.id.isPostWeibo);
        if (!"".equals( UserStore.getAccessToken(mContext))){
            isPostWeibo.setChecked(true);
        }
        mImage = (ImageView)findViewById(R.id.imageView1);

        mContent.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable s) {
              
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCount.setText(mContext.getString(R.string.comment_have_input) + mContent.length() +"/140");
            }
        });
        
        mBackButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mPostButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mContent.getText().toString().trim().length() < MIN_INPUT){
                    Toast.makeText(mContext, String.format(getString(R.string.comment_min_length), MIN_INPUT), Toast.LENGTH_LONG).show();
                    return;
                }
                
                showProgressDialog();

                Comment c = new Comment();
                c.setChannel(mChannelName);
                c.setProgram(mProgram);
                c.setContent(mContent.getText().toString());
                c.setType("1");
                c.setLocation(UserStore.getLocation(mContext));
                c.setLat(UserStore.getLat(mContext));
                c.setLon(UserStore.getLon(mContext));
                c.setScreenName(UserStore.getScreenName(mContext));
                if ("".equals(UserStore.getUserId(mContext))) {
                    c.setUserid("guest");
                } else {
                    c.setUserid(UserStore.getUserId(mContext));
                }
                c.setEmail(UserStore.getEmail(mContext));
             
                new CommentTask(getApplicationContext(), CommentTask.genUrl(c), mCallback).start();
                
                if (!"".equals( UserStore.getAccessToken(mContext)) && isPostWeibo.isChecked()){
                    
                    post(mContent.getText().toString() + msg);
                }
            }
        });
    }

    private CommentTask.Callback mCallback = new CommentTask.Callback() {

        @Override
        public void update(int status) {
            if (status < 0) {
                mHandler.sendMessage(mHandler.obtainMessage(-2, null));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(2, null));
            }
        }
    };

    private void showProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.msg_wait));
        pd.setCancelable(true);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
    }
    
    private void showProgressDialog(String msg) {
        pd = new ProgressDialog(this);
        pd.setMessage(msg);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
    }

    private void post(String content) {
        Weibo weibo = OAuthConstant.getInstance().getWeibo();
        String token = UserStore.getAccessToken(mContext);
        String secret = UserStore.getAccessSecret(mContext);
        OAuthConstant.getInstance().setToken(token);
        OAuthConstant.getInstance().setTokenSecret(secret);
        weibo.setToken(token, secret);

        try {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            Status status;
            if (null != location && null != mBitmap) {
                status = weibo.uploadStatus(content,mFile, location.getLatitude(), location
                        .getLongitude());
            } else if (null == location && null != mBitmap)  {
                status = weibo.uploadStatus(content,mFile);
            } else if (null != location && null == mBitmap)  {
                status = weibo.updateStatus(content,location.getLatitude(), location
                        .getLongitude());
            } else {
                status = weibo.updateStatus(content );
            }
            Log.d("post", "status=" + status);
            mHandler.sendMessage(mHandler.obtainMessage(1, status));

        } catch (WeiboException e) {
            mHandler.sendMessage(mHandler.obtainMessage(-1, e));
            Log.e(TAG, "" + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.weibo_post_ok, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case -1:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.comment_weibo_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.comment_succeed, Toast.LENGTH_SHORT)
                                .show();
                        finish();
                    }
                    break;
                case -2:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.comment_failed, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                    
                case 4:
                    if (null != pd) {
                        pd.dismiss();
                        mImage.setImageBitmap((Bitmap)msg.obj);
                        mImage.setVisibility(View.VISIBLE);
                        Toast.makeText(Post.this, R.string.fetch_image_success, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
                case -4:
                    if (null != pd) {
                        pd.dismiss();
                        mImage.setVisibility(View.GONE);
                        Toast.makeText(Post.this, R.string.fetch_image_failed, Toast.LENGTH_SHORT)
                                .show();
                    }
                    break;
            }
        };
    };
    
    private void doFetchImage() {
        if (null != pd && pd.isShowing()) { return;}
        showProgressDialog(getString(R.string.wait_fetch_image));
        
        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {100,200,300}, 2);
        fetchImage();
    }

    private void fetchImage() {
        if (null != mChannel && !"".equals(mChannel)) {
            new Thread() {
                public void run() {
                    
                    try {
                        URL url = new URL(IMAGE_URL +  DES.encrypt(mChannel) );
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                        conn.setConnectTimeout(30000);
                        conn.setReadTimeout(300000);
                        //conn.addRequestProperty("m", DES.encrypt(mChannel));
                        //Log.d(TAG, DES.encrypt(mChannel));
                        if (conn.getResponseCode() == 200) {
                            mBitmap = BitmapFactory.decodeStream(conn.getInputStream());
                            
                            if (null != mBitmap) {
                                Message msg = mHandler.obtainMessage();
                                msg.what = 4;
                                msg.obj = mBitmap;
                                mFile = saveBitmapFile(mBitmap,(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())) + mChannel + ".jpg");
                                Log.d(TAG, "bitmap " + mBitmap.getRowBytes() +  mFile.getAbsolutePath());

                                mHandler.sendMessage(msg);
                            }
                            else {
                                mHandler.sendEmptyMessage(-4);
                            }
                        }
                        else {
                            mHandler.sendEmptyMessage(-4);
                        }
                      

                    } catch (IOException e) {
                        mHandler.sendEmptyMessage(-4);
                        e.printStackTrace();
                    } 
                }

            }.start();
        }
    }

    
    /**
     * 把Bitmap保存为本地图片
     * 
     * @param bitmap 将要保存的bitmap
     * @return bitmap
     * @throws IOException 程序的写入异常
     */
    public File saveBitmapFile(Bitmap bitmap, String filename) throws IOException {
        if (bitmap == null) {
            Log.e(TAG, "saveMyBitmap failed,the bitmap is null");
            return null;
        }

        FileOutputStream out = null;
        File file = new File(IMG_FILE_PATH + filename);
        try {
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs())// 创建目录
                {
                    // 如果不存在,创建又失败了,则说明无法创建目录
                    return null;
                }
            }

            // 创建文件
            out = new FileOutputStream(file);

            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)) {
                out.flush();
                // bitmap.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            file = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeStream(out);
        }

        return file;
    }

}
