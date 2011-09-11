
package com.offbye.chinatvguide;

import com.offbye.chinatvguide.util.AssetUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ChannelAdapter extends ArrayAdapter<TVChannel> {

    int resource;

    Context mContext;

    public ChannelAdapter(Context _context, int _resource, List<TVChannel> _items) {
        super(_context, _resource, _items);
        mContext = _context;
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout channelView;
        TVChannel channel = getItem(position);

        if (convertView == null) {
            channelView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, channelView, true);
        }
        else
        {
            channelView = (LinearLayout) convertView;
        }

        ImageView channellogo = (ImageView) channelView.findViewById(R.id.channellogo);

        TextView channelname = (TextView) channelView.findViewById(R.id.channelname);
        channellogo.setImageBitmap(AssetUtil.getImageFromAssetFile(mContext, channel.getChannel()
                + ".png"));

        channelname.setText(channel.getChannelname());
        ImageView favouriteicon = (ImageView) channelView.findViewById(R.id.favouriteicon);
        if (channel.getHidden().equals("1")) {
            favouriteicon.setVisibility(View.VISIBLE);
        } else {
            favouriteicon.setVisibility(View.GONE);
        }

        return channelView;
    }
}
