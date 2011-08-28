
package com.offbye.chinatvguide.server;

import com.offbye.chinatvguide.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<Comment> {
    int mResource;

    Context mContext;

    public CommentAdapter(Context context, int resource, List<Comment> items) {
        super(context, resource, items);
        mContext = context;
        mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout view;
        Comment item = getItem(position);
        if (convertView == null) {
            view = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(mResource, view, true);
        } else {
            view = (LinearLayout) convertView;
        }
        TextView dateView = (TextView) view.findViewById(R.id.time);
        TextView screennameView = (TextView) view.findViewById(R.id.screenname);
        TextView channelView = (TextView) view.findViewById(R.id.channel);
        TextView contentView = (TextView) view.findViewById(R.id.content);

        dateView.setText(getDistanceTime(item.getTime()));

        String user = "".equals(item.getScreenName()) ? "游客" : item.getScreenName();
        screennameView.setText(Html.fromHtml( "<font color=green><b>" +user +"</d></font> @ " + item.getLocation()));
        if ("0".equals(item.getType())) {
            contentView.setVisibility(View.GONE);
            channelView.setText(user + " 正在看 " + item.getChannel() + "  " + item.getProgram());
        } else {
            contentView.setText(item.getContent());
            
            if(null != item.getChannel() && !"".equals(item.getChannel()) 
                    && null != item.getProgram() && !"".equals(item.getProgram())){
                channelView.setText(user + " 评论了  " + item.getChannel() + "  " + item.getProgram());
            }
            else {
                channelView.setVisibility(View.GONE);
            }
        }
        return view;
    }

    public static String getDistanceTime(String str1) {
        String re = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            one = df.parse(str1);
            long time1 = one.getTime();
            long time2 = new Date().getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                //diff = time1 - time2;
                return str1;  //small than now not compute.
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day > 2) {
            re = str1.substring(0, 10);
        } else {
            if (day > 0) {
                re = day + "天前";
            } else if (hour > 0) {
                re = hour + "小时前";
            } else if (min > 0) {
                re = min + "分钟前";
            } else {
                re = "刚刚";
            }
        }
        return re;
    }
}
