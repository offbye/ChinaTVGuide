package com.offbye.chinatvguide.rss;

import java.io.InputStream;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offbye.chinatvguide.R;

public class RSSAdapter extends ArrayAdapter<RSSItem> {
	int resource;
	Context context;

	public RSSAdapter(Context _context, int _resource,
			List<RSSItem> _items) {
		super(_context, _resource, _items);
		context=_context;
		resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout channelView;
		RSSItem item = getItem(position);

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
		title.setText(item.getTitle());
		
		TextView pubdate = (TextView) channelView.findViewById(R.id.pubdate);
		pubdate.setText(item.getPubDate());
		TextView description = (TextView) channelView.findViewById(R.id.description);
		description.setText(item.getDescription());
		
		return channelView;
	}
}
