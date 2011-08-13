package com.offbye.chinatvguide.favorite;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import com.offbye.chinatvguide.R;


public class FavouriteTab extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator(this.getString(R.string.tab_favouritechannel), getResources().getDrawable(R.drawable.favouritetab))
                .setContent(new Intent(this, FavouriteChannelActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator(this.getString(R.string.tab_favouriteprogram),getResources().getDrawable(R.drawable.favourite_program))
                .setContent(new Intent(this, FavoriteProgramActivity.class)));

    }
}
