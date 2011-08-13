package com.offbye.chinatvguide.rss;


import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offbye.chinatvguide.R;

public class RSSChannelAdapter extends ArrayAdapter<RSSChannel> {
	int resource;
	Context context;

	public RSSChannelAdapter(Context _context, int _resource,
			List<RSSChannel> _items) {
		super(_context, _resource, _items);
		context=_context;
		resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout channelView;
		RSSChannel item = getItem(position);

		if (convertView == null)
		{
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

		TextView title = (TextView) channelView.findViewById(R.id.title);
		title.setText(item.title);
		
		TextView url = (TextView) channelView.findViewById(R.id.url);
		url.setText(item.image);
		
		
		return channelView;
	}

}
