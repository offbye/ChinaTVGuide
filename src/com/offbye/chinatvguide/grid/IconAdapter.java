package com.offbye.chinatvguide.grid;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offbye.chinatvguide.R;


public class IconAdapter extends ArrayAdapter<Icon> {
	
	int mGalleryItemBackground;
	private static final String TAG = "IconAdapter";
	int resource;
	Context context;
	
    public IconAdapter(Context _context, int _resource,
    		ArrayList<Icon> _items) {
		super(_context, _resource,  _items);
		context = _context;
		resource = _resource;

	}


    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	LinearLayout iconView;

    	Icon ic = getItem(position);

		if (convertView == null)

		{

			iconView = new LinearLayout(getContext());

			String inflater = Context.LAYOUT_INFLATER_SERVICE;

			LayoutInflater vi;

			vi = (LayoutInflater) getContext().getSystemService(inflater);

			vi.inflate(resource, iconView, true);

		}

		else

		{

			iconView = (LinearLayout) convertView;

		}

		ImageView icon = (ImageView) iconView
				.findViewById(R.id.icon);

		TextView title = (TextView) iconView
				.findViewById(R.id.title);
		icon.setBackgroundResource(ic.image);
		title.setText(ic.title);
		//iconView.setLayoutParams(new GridView.LayoutParams(80, 80));
		//iconView.setAdjustViewBounds(false);
		//iconView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		//iconView.setPadding(6, 6, 6, 6);
		return iconView;		
    }
}