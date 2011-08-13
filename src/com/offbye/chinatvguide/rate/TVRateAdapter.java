package com.offbye.chinatvguide.rate;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.offbye.chinatvguide.R;

public class TVRateAdapter extends ArrayAdapter<TVRate> {
	int resource;
	Context context;
	public TVRateAdapter(Context _context, int _resource,
			List<TVRate> _items) {
		super(_context, _resource, _items);
		context=_context;
		resource = _resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LinearLayout channelView;
		TVRate item = getItem(position);

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
		TextView rank = (TextView) channelView.findViewById(R.id.rank);
		rank.setText(item.rank);
		TextView program = (TextView) channelView.findViewById(R.id.program);
		program.setText(item.program);
		TextView channel = (TextView) channelView.findViewById(R.id.channel);
		channel.setText(item.channel);
		TextView changes = (TextView) channelView.findViewById(R.id.changes);
		changes.setText(item.changes);
		TextView averagerate = (TextView) channelView.findViewById(R.id.averagerate);
		averagerate.setText(item.averageRate);
		
		return channelView;
	}

}
