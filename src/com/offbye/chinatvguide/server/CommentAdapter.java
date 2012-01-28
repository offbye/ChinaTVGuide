
package com.offbye.chinatvguide.server;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.util.DateUtil;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        dateView.setText(DateUtil.getPassedTime(item.getTime()));

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

   
}
