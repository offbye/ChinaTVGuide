package com.offbye.chinatvguide.grid;

import com.offbye.chinatvguide.CurrentProgramView;
import com.offbye.chinatvguide.PreferencesActivity;
import com.offbye.chinatvguide.R;
import com.offbye.chinatvguide.SearchResultView;
import com.offbye.chinatvguide.SearchView;
import com.offbye.chinatvguide.SuggestView;
import com.offbye.chinatvguide.channel.ChannelTab;
import com.offbye.chinatvguide.favorite.FavouriteTab;
import com.offbye.chinatvguide.rate.TVRateActivity;
import com.offbye.chinatvguide.recommend.TVRecommendActivity;
import com.offbye.chinatvguide.rss.RSSActivity;
import com.offbye.chinatvguide.server.user.UserInfoActivity;
import com.offbye.chinatvguide.util.Shortcut;
import com.offbye.chinatvguide.weibo.WeiboCheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Grid extends Activity {
	private ArrayList<Icon> icons = new ArrayList<Icon>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.grid);

		icons.add(new Icon(R.drawable.grid_channel, R.string.grid_channel));
		icons.add(new Icon(R.drawable.grid_broadcasting,
				R.string.grid_broadcasting));
		icons.add(new Icon(R.drawable.grid_search, R.string.grid_search));

		icons.add(new Icon(R.drawable.grid_rss, R.string.grid_rss));
		icons.add(new Icon(R.drawable.grid_favourite, R.string.grid_favourite));
		icons.add(new Icon(R.drawable.grid_comment, R.string.grid_recommend));

		icons.add(new Icon(R.drawable.grid_rank, R.string.grid_rank));
		icons.add(new Icon(R.drawable.grid_suggest, R.string.submitsuggest));
		icons.add(new Icon(R.drawable.grid_icon, R.string.grid_about));

		GridView g = (GridView) findViewById(R.id.myGrid);
		g.setAdapter(new IconAdapter(this, R.layout.grid_row, icons));
		g.setNumColumns(3);
		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				if (position == 0) {
					Intent i = new Intent(Grid.this, ChannelTab.class);
					startActivity(i);
				} else if (position == 1) {
					Intent i = new Intent(Grid.this, CurrentProgramView.class);
					startActivity(i);
				} else if (position == 2) {
					Intent i = new Intent(Grid.this, SearchView.class);
					startActivity(i);
				}
				// 第二行
				else if (position == 3) {
					Intent i = new Intent(Grid.this, RSSActivity.class);
					startActivity(i);
				} else if (position == 4) {
					Intent i = new Intent(Grid.this, FavouriteTab.class);
					startActivity(i);
				} else if (position == 5) {
					Intent i = new Intent(Grid.this, TVRecommendActivity.class);
					startActivity(i);
				}
				// 第三行
				else if (position == 6) {
					Intent i = new Intent(Grid.this, TVRateActivity.class);
					startActivity(i);
				} else if (position == 7) {
					Intent i = new Intent(Grid.this, SuggestView.class);
					startActivity(i);
				} else if (position == 8) {
					new AlertDialog.Builder(Grid.this).setIcon(R.drawable.icon)
					.setTitle(R.string.menu_abouttitle).setMessage(
							R.string.aboutinfo).setPositiveButton(
							R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {

								}
							}).show();
				}
			}
		});
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean firststart = prefs.getBoolean("firststart", true);
		if (firststart) {
			new AlertDialog.Builder(Grid.this).setIcon(R.drawable.icon)
					.setTitle(R.string.create_shortcut).setMessage(
							R.string.create_shortcut_body).setPositiveButton(
							R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Shortcut.addShortCut(Grid.this, ".ChinaTVGuide");
								}
							}).setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).show();

			prefs.edit().putBoolean("firststart", false).commit();
		}
		
		final EditText program = (EditText)findViewById(R.id.SearchText);
		ImageButton search =  (ImageButton)this.findViewById(R.id.search);
		Date date=new Date();
        SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
        final String currentDate=df.format(date);
        
		search.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                String text = program.getText().toString().trim();
                if("".equals(text)){
                    Toast.makeText(Grid.this, R.string.search_not_blank, Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent i = new Intent(Grid.this,SearchResultView.class);
                    i.putExtra("channel", "all");
                    i.putExtra("program", text);
                    i.putExtra("cdate",currentDate);
                    i.putExtra("starttime", "");
                    i.putExtra("notsearchtime", true);
                    startActivity(i);
                }
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.weibo_menu)).setIcon(R.drawable.weibo);
		menu.add(0, 1, 1,  this.getText(R.string.menu_help)).setIcon(R.drawable.ic_menu_help);
		menu.add(0, 2, 2,  this.getText(R.string.menu_exit)).setIcon(R.drawable.ic_menu_close_clear_cancel);
		menu.add(0, 3, 3,  this.getText(R.string.preferences_name)).setIcon(R.drawable.ic_menu_preferences);
		menu.add(0, 4, 4,  this.getText(R.string.user_info)).setIcon(R.drawable.ic_menu_edit);
		if (10 < Integer.valueOf(Build.VERSION.SDK)) {
			menu.getItem(0).setShowAsAction(1);
			menu.getItem(1).setShowAsAction(1);
			menu.getItem(2).setShowAsAction(1);
		}
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			addWeibo("");
			break;
		case 1:
			new AlertDialog.Builder(this)
			.setIcon(R.drawable.icon)
			.setTitle(R.string.menu_help)
			.setMessage(R.string.helpinfo)
			.setPositiveButton(this.getText(R.string.alert_dialog_ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					}).show();
			break;

		case 2:
			finish();
			Shortcut.exit(this);
			break;
		case 3:
		    Intent it = new Intent();
	        it.setClass(this, PreferencesActivity.class);
	        this.startActivity(it);
            break;
		
	    case 4:
            Intent it2 = new Intent();
            it2.setClass(this, UserInfoActivity.class);
            this.startActivity(it2);
            break;
        }
		return super.onOptionsItemSelected(item);
	}

	private void addWeibo(String msg) {
		Intent it = new Intent();
		it.setClass(getApplicationContext(), WeiboCheck.class);
		this.startActivity(it);
	}
}
