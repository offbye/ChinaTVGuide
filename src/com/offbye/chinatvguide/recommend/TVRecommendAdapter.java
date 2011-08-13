package com.offbye.chinatvguide.recommend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.TVProgram;

public class TVRecommendAdapter extends ArrayAdapter<TVProgram> {
	int resource;
	Context context;
	public TVRecommendAdapter(Context _context, int _resource,
			List<TVProgram> _items) {
		super(_context, _resource, _items);
		context=_context;
		resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout todoView;
		TVProgram item = getItem(position);
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
		TextView programView = (TextView) todoView.findViewById(R.id.program);
		TextView channelnameView = (TextView) todoView
				.findViewById(R.id.channelname);
		channelnameView.setText(item.getChannelname().trim());
		dateView.setText(item.getDate()+ "  "+ item.getStarttime());
		programView.setText(item.getProgram().trim());
		return todoView;
	}
}
