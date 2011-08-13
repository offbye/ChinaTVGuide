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

public class CurrentProgramAdapter extends ArrayAdapter<TVProgram> {

	int resource;
	Context context;

	public CurrentProgramAdapter(Context _context, int _resource,
			List<TVProgram> _items) {

		super(_context, _resource, _items);
		context = _context;
		resource = _resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)

	{

		LinearLayout todoView;

		TVProgram item = getItem(position);

		String startime = item.getStarttime();

		String program = item.getProgram();

		if (convertView == null)

		{

			todoView = new LinearLayout(getContext());

			String inflater = Context.LAYOUT_INFLATER_SERVICE;

			LayoutInflater vi;

			vi = (LayoutInflater) getContext().getSystemService(inflater);

			vi.inflate(resource, todoView, true);

		}

		else

		{

			todoView = (LinearLayout) convertView;

		}

		TextView dateView = (TextView) todoView.findViewById(R.id.startime);

		TextView taskView = (TextView) todoView.findViewById(R.id.program);
		ImageView channellogo = (ImageView) todoView
				.findViewById(R.id.channellogo);
		channellogo.setImageBitmap(getImageFromAssetFile(item.getChannel()
				+ ".png"));

		dateView.setText(startime + " -- " +item.getEndtime());

		taskView.setText(program);

		return todoView;

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
