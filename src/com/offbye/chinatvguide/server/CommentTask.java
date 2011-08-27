package com.offbye.chinatvguide.server;

import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;

public class CommentTask extends Thread {
    private static final String TAG = "CommentTask"; 
    private Context mContext;

    private String mUrl;

    private Callback mCallback;

    public CommentTask(Context context, String url, Callback callback) {
        mContext = context;
        mUrl = url;
        mCallback = callback;
    }

    public void run() {
        try {
            String sb = HttpUtil.getUrl(mContext, mUrl);
            Log.d(TAG,sb);
            mCallback.update(Integer.valueOf(sb));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mCallback.update(-3);
        } catch (AppException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mCallback.update(-2);
        }
    }

    public static String genUrl(Comment c) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.URL_COMMENT);
        sb.append("?");
        sb.append("userid=");
        sb.append(c.getUserid());
        if (null != c.getContent() && !"".equals(c.getContent())) {
            sb.append("&content=");
            sb.append(URLEncoder.encode(c.getContent()));
        }
        if (null != c.getChannel() && !"".equals(c.getChannel())) {
            sb.append("&channel=");
            sb.append(URLEncoder.encode(c.getChannel()));
        }

        if (null != c.getProgram() && !"".equals(c.getProgram())) {
            sb.append("&program=");
            sb.append(URLEncoder.encode(c.getProgram()));
        }
        if (null != c.getScreenName() && !"".equals(c.getScreenName())) {
            sb.append("&screenname=");
            sb.append(URLEncoder.encode(c.getScreenName()));
        }
        if (null != c.getLocation() && !"".equals(c.getLocation())) {
            sb.append("&location=");
            sb.append(URLEncoder.encode(c.getLocation()));
        }
        sb.append("&type=");
        sb.append(c.getType());

        return sb.toString();
    }

    public interface Callback {
        void update(int status);
    }
}
