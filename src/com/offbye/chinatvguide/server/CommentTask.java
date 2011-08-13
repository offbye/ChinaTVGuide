package com.offbye.chinatvguide.server;

import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Constants;
import com.offbye.chinatvguide.util.HttpUtil;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

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
            mCallback.update(sb);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mCallback.update(e);
        } catch (AppException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mCallback.update(e);
        }
    }

    public static String genUrl(Comment c) {
        StringBuilder sb = new StringBuilder();
        sb.append(Constants.URL_COMMENT);
        sb.append("?");
        sb.append("userid=");
        sb.append(c.userid);
        sb.append("&content=");
        sb.append(c.content);
        sb.append("&channel=");
        sb.append(c.channel);
        sb.append("&program=");
        sb.append(c.program);
        sb.append("&screenname=");
        sb.append(c.screenName);
        sb.append("&type=");
        sb.append(c.type);

        return sb.toString();
    }

    public interface Callback {
        void update(Object message);
    }
}
