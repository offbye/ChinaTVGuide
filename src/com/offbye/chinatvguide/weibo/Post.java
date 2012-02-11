
package com.offbye.chinatvguide.weibo;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.TVProgram;
import com.offbye.chinatvguide.server.Comment;
import com.offbye.chinatvguide.server.CommentTask;
import com.offbye.chinatvguide.server.user.UserStore;
import com.offbye.chinatvguide.util.FileUtil;
import com.offbye.chinatvguide.util.HttpUtil;

import weibo4android.Status;
import weibo4android.Weibo;
import weibo4android.WeiboException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class Post extends Activity {

    private final static String TAG = "Post";
    private static final String SEND_IMG_FILE_PATH = "/sdcard/chinatvguide/sendImg.png";

    private static final String IMAGE_URL = "http://m.intotv.net/sc/s.php?c=";

    static Object lock = new Object();

    private TextView mCount;

    private EditText mContent;

    private Button mPost;
    private CheckBox isPostWeibo;
    private ImageView mImage;
    private ProgressDialog pd;
    private String mChannel;
    private String mChannelName;

    private String mProgram;
    private String msg;

    private Context mContext;
    
    private int count=0;
    private SensorManager sensorManager = null;
    private Sensor sensor = null;
    private int countx = 0;
    private int countxm = 0;
    private int county = 0;
    private int countym = 0;
    private int countz = 0;
    private int countzm = 0;
    private Bitmap mBitmap = null;
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
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        
        System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
        System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);

        init();
        mChannel = getIntent().getStringExtra("channel");
        mChannelName = getIntent().getStringExtra("channelname");
        mProgram = getIntent().getStringExtra("program");
        
        msg = "";
        if (null != mChannelName && !"".equals(mChannelName) && null != mProgram && !"".equals(mProgram)){
            msg = "\n " + mContext.getString(R.string.comment) + " #"
            + mChannelName.trim() + "#, #" + mProgram.trim() + "#";
            mContent.setHint(msg);
        }
        else if (null != mChannelName && !"".equals(mChannelName)){
            msg = "\n " + mContext.getString(R.string.comment) + " #"
            + mChannelName.trim() + "#";
            mContent.setHint(msg);
        }

    }
    
    @Override
    protected void onResume() {
        super.onResume();
        register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregister();
    }

    private void init() {
        mCount = (TextView) findViewById(R.id.count);
        mContent = (EditText) findViewById(R.id.content);
        mPost = (Button) findViewById(R.id.post);
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
            }});
        mPost.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mContent.getText().toString().trim().length() < 10){
                    Toast.makeText(mContext, R.string.comment_min_length, Toast.LENGTH_LONG).show();
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
        pd.setCancelable(true);
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
                status = weibo.uploadStatus(content,saveMyBitmap(mBitmap), location.getLatitude(), location
                        .getLongitude());
            } else if (null == location && null != mBitmap)  {
                status = weibo.uploadStatus(content,saveMyBitmap(mBitmap));
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
                        Toast.makeText(Post.this, R.string.comment_weibo_failed,
                                Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    break;
                case 2:
                    if (null != pd) {
                        pd.dismiss();
                        Toast.makeText(Post.this, R.string.comment_succeed, Toast.LENGTH_SHORT)
                                .show();
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
    
    
    SensorEventListener listener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent e) {
            float x = e.values[SensorManager.DATA_X];
            float y = e.values[SensorManager.DATA_Y];
            float z = e.values[SensorManager.DATA_Z];

            process(x, y, z);
        }

        public void onAccuracyChanged(Sensor s, int accuracy) {
        }
    };

    public void process(float x, float y, float z) {
        if (x > 3 ) {
            countx++;
            //Toast.makeText(HomeActivity.this, "countx" + countx, 5).show();
        }
        if (x < -3 ) {
            countxm++;
            //Toast.makeText(HomeActivity.this, "countx" + countx, 5).show();
        }
        if (y > 3) {
            county++;
        }
        if (y < -3) {
            countym++;
        }
        if (z - SensorManager.GRAVITY_EARTH > 4) {
            countz++;
        }
        if((countx>3 && countxm>3) || (county>3 && countym>3) || countz>3){
            countx=0;
            countxm=0;
            county=0;
            countym=0;
            countz=0;
            countzm=0;
            
            if (null != pd && pd.isShowing()) { return;}
            showProgressDialog(getString(R.string.wait_fetch_image));
            
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[] {100,200,300}, -1);
            fetchImage();
        }
    }

    void register() {
        sensorManager.registerListener(listener, sensor,
                SensorManager.SENSOR_DELAY_UI);
    }

    void unregister() {
        sensorManager.unregisterListener(listener);
    }
    
    private void fetchImage() {
        if (null != mChannel && !"".equals(mChannel)) {
            new Thread() {
                public void run() {
                    
                    try {
                        mBitmap = BitmapFactory.decodeStream(HttpUtil.getURLStream(IMAGE_URL+mChannel+"&d="+new Date()));
                        
                        if (null != mBitmap) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 4;
                            msg.obj = mBitmap;
                            Log.d(TAG, "bitmap " + mBitmap.getRowBytes());
                            mHandler.sendMessage(msg);
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
    
    private byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
    
    /**
     * 把Bitmap保存为本地图片
     * 
     * @param bitmap 将要保存的bitmap
     * @return bitmap
     * @throws IOException 程序的写入异常
     */
    public File saveMyBitmap(Bitmap bitmap) throws IOException
    {
        if (bitmap == null)
        {
            Log.e(TAG,
                "saveMyBitmap failed,the bitmap is null");
            return null;
        }

        FileOutputStream out = null;
        File file = new File(SEND_IMG_FILE_PATH);
        try
        {
            if (!file.getParentFile().exists())
            {
                if (file.getParentFile().mkdirs())// 创建目录
                {
                    // 如果不存在,创建又失败了,则说明无法创建目录
                    return null;
                }
            }

            // 创建文件
            out = new FileOutputStream(file);

            if (bitmap.compress(Bitmap.CompressFormat.PNG,
                70,
                out))
            {
                out.flush();
                bitmap.recycle();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            file = null;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            FileUtil.closeStream(out);
        }

        return file;
    }

}
