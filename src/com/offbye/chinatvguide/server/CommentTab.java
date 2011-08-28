package com.offbye.chinatvguide.server;

import com.offbye.chinatvguide.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class CommentTab extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(this.getString(R.string.latest_checkin), getResources().getDrawable(R.drawable.ic_tab_checkin))
                .setContent(new Intent(this, CommentList.class).putExtra("type", "0")));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(this.getString(R.string.latest_comment), getResources().getDrawable(R.drawable.ic_tab_comment))
                .setContent(new Intent(this, CommentList.class).putExtra("type", "1")));

    }
}
