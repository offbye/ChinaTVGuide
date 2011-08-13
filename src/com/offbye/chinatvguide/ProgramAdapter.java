package com.offbye.chinatvguide;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class ProgramAdapter extends ArrayAdapter<TVProgram> {
	private static final String TAG = "ProgramAdapter";
	int resource;
	private Calendar calendar = Calendar.getInstance();
     

	public ProgramAdapter(Context _context, int _resource,
			List<TVProgram> _items) {

		super(_context, _resource, _items);

		resource = _resource;
		calendar.setTimeInMillis(System.currentTimeMillis());

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
		ImageView daynight = (ImageView) todoView.findViewById(R.id.daynight);
		

		
		if(item.getDaynight().equals("d")){
			daynight.setImageResource(R.drawable.day);
		} else if(item.getDaynight().equals("c")){
		    daynight.setImageResource(R.drawable.play);
		}
		else{
			daynight.setImageResource(R.drawable.night);
		}

		dateView.setText(startime);
		taskView.setText(program);
//		int starthour=Integer.parseInt(item.getStarttime().split(":")[0]);
//		if(calendar.get(Calendar.HOUR_OF_DAY) == starthour){
//			dateView.setTextColor(Color.GREEN);
//			taskView.setTextColor(Color.GREEN);
//			//Log.v(TAG,"calendar.get(Calendar.HOUR_OF_DAY):"+calendar.get(Calendar.HOUR_OF_DAY));
//		}
		return todoView;

	}

}
