package com.offbye.chinatvguide;

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

public class ChannelAdapter extends ArrayAdapter<TVChannel> {

	int resource;
	Context context;

	public ChannelAdapter(Context _context, int _resource,
			List<TVChannel> _items) {

		super(_context, _resource, _items);
		context=_context;
		resource = _resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)

	{

		LinearLayout channelView;

		TVChannel channel = getItem(position);

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

		ImageView channellogo = (ImageView) channelView
				.findViewById(R.id.channellogo);

		TextView channelname = (TextView) channelView
				.findViewById(R.id.channelname);
		channellogo.setImageBitmap(getImageFromAssetFile(channel.getChannel()+".png"));

		channelname.setText(channel.getChannelname());
		ImageView favouriteicon = (ImageView) channelView.findViewById(R.id.favouriteicon);
		if(channel.getHidden().equals("1")){
			favouriteicon.setVisibility(View.VISIBLE);
		}
		else
		{
			favouriteicon.setVisibility(View.GONE);
		}
	
		return channelView;

	}

	private Bitmap getImageFromAssetFile(String fileName) {
		Bitmap image = null;
		try {
			AssetManager am = context.getAssets();
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {

		}
		return image;
	}

}
