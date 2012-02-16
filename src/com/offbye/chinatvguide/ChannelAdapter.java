
package com.offbye.chinatvguide;

import com.offbye.chinatvguide.server.media.MediaStore;
import com.offbye.chinatvguide.util.AssetUtil;
import com.offbye.chinatvguide.util.SystemUtility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
        final TVChannel channel = getItem(position);

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
        
        ImageView play = (ImageView) channelView.findViewById(R.id.playicon);
        /*if (channel.isPlayable) {
            play.setVisibility(View.VISIBLE);
            play.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int arch = SystemUtility.getArmArchitecture();
                    Log.i("ChannelAdapter", "getArmArchitecture" + arch);
                    try {
                        Intent i =new Intent("com.offbye.chinatvguide.player");
                        i.putStringArrayListExtra("playlist", MediaStore.getInstance().getHrefs(channel.getChannel()));
                        i.putStringArrayListExtra("titlelist", MediaStore.getInstance().getTitles(channel.getChannel()));
                        mContext.startActivity(i);
                    }
                    catch (Exception e){
                       
                        new AlertDialog.Builder(mContext)
                        .setIcon(R.drawable.icon)
                        .setTitle(R.string.tvplayer_downaload)
                        .setMessage(R.string.tvplayer_install)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (arch > 6) {
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pecansoft.com/app/chinatvguidelive_n.apk"));
                                    mContext.startActivity(i); 
                                }
                                else {
                                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.pecansoft.com/app/chinatvguidelive_a.apk"));
                                    mContext.startActivity(i); 
                                }
                            }
                        }).show();
                      
                    }

                }
            });
        } else {
            play.setVisibility(View.GONE);
        }
        */

        return channelView;
    }
}
