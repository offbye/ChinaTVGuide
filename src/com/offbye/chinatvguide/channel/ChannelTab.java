package com.offbye.chinatvguide.channel;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.local.LocalCurrentProgramView;

public class ChannelTab extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent cctv = new Intent(ChannelTab.this, ChannelActivity.class); 
        cctv.putExtra("type", "cctv");
        Intent sjws = new Intent(ChannelTab.this, ChannelActivity.class); 
        sjws.putExtra("type", "sjws");
        Intent gt = new Intent(ChannelTab.this, ChannelActivity.class); 
        gt.putExtra("type", "gt");
        Intent sz = new Intent(ChannelTab.this, ChannelActivity.class); 
        sz.putExtra("type", "sz");
        
        
        final TabHost tabHost = getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(this.getString(R.string.tab_cctv), getResources().getDrawable(R.drawable.ic_tab_cctv))
                .setContent(cctv));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(this.getString(R.string.tab_sjws),getResources().getDrawable(R.drawable.ic_tab_sjws))
                .setContent(sjws));
        tabHost.addTab(tabHost.newTabSpec("tab3")
                .setIndicator(this.getString(R.string.tab_gt),getResources().getDrawable(R.drawable.ic_tab_gt))
                .setContent(gt));
        tabHost.addTab(tabHost.newTabSpec("tab4")
                .setIndicator(this.getString(R.string.tab_shuzhi),getResources().getDrawable(R.drawable.ic_tab_shuzi))
                .setContent(sz));
        tabHost.addTab(tabHost.newTabSpec("tab5")
                .setIndicator(this.getString(R.string.tab_local),getResources().getDrawable(R.drawable.ic_tab_local))
                .setContent(new Intent(this,LocalCurrentProgramView.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
    }
}
