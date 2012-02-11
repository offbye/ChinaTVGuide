
package com.offbye.chinatvguide;

import com.offbye.chinatvguide.server.media.MediaStore;
import com.offbye.chinatvguide.util.AssetUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class CurrentProgramAdapter extends ArrayAdapter<TVProgram> {

    int resource;

    Context context;

    public CurrentProgramAdapter(Context _context, int _resource, List<TVProgram> _items) {

        super(_context, _resource, _items);
        context = _context;
        resource = _resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout todoView;
        final TVProgram item = getItem(position);
        String startime = item.getStarttime();
        String program = item.getProgram();

        if (convertView == null) {
            todoView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, todoView, true);
        } else {
            todoView = (LinearLayout) convertView;
        }

        TextView dateView = (TextView) todoView.findViewById(R.id.startime);

        TextView programView = (TextView) todoView.findViewById(R.id.program);
        ImageView channellogo = (ImageView) todoView.findViewById(R.id.channellogo);
        channellogo.setImageBitmap(AssetUtil.getImageFromAssetFile(context, item.getChannel()
                + ".png"));

        dateView.setText(startime + " -- " + item.getEndtime());
        programView.setText(program);

        ImageView play = (ImageView) todoView.findViewById(R.id.playicon);
        if (item.isPlayable) {
            play.setVisibility(View.VISIBLE);
            play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent("com.offbye.chinatvguide.player");
                    i.putStringArrayListExtra("playlist", MediaStore.getInstance().getHrefs(item.getChannel()));
                    i.putStringArrayListExtra("titlelist", MediaStore.getInstance().getTitles(item.getChannel()));
                    context.startActivity(i);
                }
            });
        } else {
            play.setVisibility(View.GONE);
        }
        return todoView;
    }
}
