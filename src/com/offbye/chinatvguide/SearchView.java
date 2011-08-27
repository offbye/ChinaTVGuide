package com.offbye.chinatvguide;


import com.offbye.chinatvguide.channel.ChannelTab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;


public class SearchView extends Activity {
	private static final String TAG = "SearchView";
	private Button search=null;
	private EditText program;
	private TextView starttime,cdate;
	private Spinner optionsListView;
	private ArrayList<TVChannel> channelList;
	private MydbHelper mydb;
	private String selectedchannel="all";
	private CheckBox notsearchtime;
	
    // date and time
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    static final int TIME_DIALOG_ID = 0;
    static final int DATE_DIALOG_ID = 1;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.searchview);
        mydb = new MydbHelper(this);
        optionsListView = (Spinner) this.findViewById(R.id.channel);
        program = (EditText) this.findViewById(R.id.program);
        cdate = (Button) this.findViewById(R.id.cdate);
        starttime = (Button) this.findViewById(R.id.starttime);
        notsearchtime = (CheckBox) this.findViewById(R.id.notsearchtime);
        
        search = (Button) this.findViewById(R.id.search);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        updateDisplay();
        //starttime.setText("00:00");
        showChannels();
		mydb.close();
        
        cdate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });

        starttime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });
        
        
        search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent i = new Intent(SearchView.this,SearchResultView.class);
                Log.i(TAG,"optionsListView.getSelectedItem().toString()="+optionsListView.getSelectedItem().toString());
                i.putExtra("channel", selectedchannel);
                i.putExtra("program", program.getText().toString().trim());
				i.putExtra("cdate", cdate.getText().toString().replaceAll("-", ""));
				i.putExtra("starttime", starttime.getText().toString());
				i.putExtra("notsearchtime", notsearchtime.isChecked());
				//Log.v(TAG,"notsearchtime.isChecked()"+notsearchtime.isChecked());
				startActivity(i);
				//finish();
			}
		});
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                            mDateSetListener,
                            mYear, mMonth, mDay);
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case TIME_DIALOG_ID:
                ((TimePickerDialog) dialog).updateTime(mHour, mMinute);
                break;
            case DATE_DIALOG_ID:
                ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
                break;
        }
    }    

    private void updateDisplay() {
    	String month="";
    	String day="";
    	if(mMonth<9){
    		month="0"+(mMonth + 1);
		}
    	else{
    		month=Integer.toString(mMonth+1);
    	}
    	if(mDay<10){
    		day="0"+mDay;
    	}
    	else{
    		day=Integer.toString(mDay);
    	}
    	
        cdate.setText(
        		
            new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mYear).append("-")
                    .append(month).append("-")
                    .append(day).append("")
        			);
        starttime.setText(
                new StringBuilder()
                        .append(pad(mHour)).append(":")
                        .append(pad(mMinute)));
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {

                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    updateDisplay();
                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    
    private void showChannels() {

		Cursor channellCursor = mydb.fetchChannels();
		startManagingCursor(channellCursor);
		channelList=new ArrayList<TVChannel>();

		//添加全部频道
		TVChannel tvchannelall=new  TVChannel(
				"0","all",
				"全部频道","",
				"","");
		channelList.add(tvchannelall);
		
		while(channellCursor.moveToNext()){
			TVChannel tvchannel=new  TVChannel(
					channellCursor.getString(0),channellCursor.getString(1),
					channellCursor.getString(2),channellCursor.getString(3),
					channellCursor.getString(4),channellCursor.getString(5));
			//Log.i(TAG,channellCursor.getString(2));
			channelList.add(tvchannel);
		}
		channellCursor.close();

		ArrayAdapter<TVChannel> channelAdapter =new  ArrayAdapter<TVChannel>(this, android.R.layout.simple_spinner_item, channelList);
		channelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		optionsListView.setAdapter(channelAdapter);

		optionsListView.setOnItemSelectedListener(
		            new OnItemSelectedListener() {
		                public void onItemSelected(
		                        AdapterView<?> parent, View view, int position, long id) {
		                	selectedchannel = channelList.get(position).getChannel();
		                }

		                public void onNothingSelected(AdapterView<?> parent) {
		                    setDefaultKeyMode(DEFAULT_KEYS_DISABLE);
		                }
		            });
		 
	}
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, this.getText(R.string.menu_about)).setIcon(R.drawable.icon);
		menu.add(0, 1, 1, this.getText(R.string.menu_channel)).setIcon(R.drawable.ic_menu_channel);
		menu.add(0, 2, 2,  this.getText(R.string.menu_help)).setIcon(android.R.drawable.ic_menu_help);

		menu.add(0, 3, 3, this.getText(R.string.menu_sync)).setIcon(R.drawable.ic_menu_refresh);
		menu.add(0, 4, 4, this.getText(R.string.menu_clean)).setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, 5, 5,  this.getText(R.string.menu_exit)).setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.icon)
					.setTitle(R.string.menu_abouttitle)
					.setMessage(R.string.aboutinfo)
					.setPositiveButton(this.getText(R.string.alert_dialog_ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

								}
							}).show();

			break;
		case 1:
			startActivity(new Intent(this,ChannelTab.class));
			finish();
			break;
		case 2:
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
		case 3:
		    this.startService(new Intent(this, SyncService.class));   
			break;
		case 4:
			 MydbHelper mydb =new MydbHelper(this);
	         if(mydb.deleteAllPrograms()==true){
	        	 Toast.makeText(this, R.string.program_data_deleted, Toast.LENGTH_LONG).show();
	         }
	         else{
	        	 Toast.makeText(this, R.string.no_data_deleted, Toast.LENGTH_LONG).show();
	         }
	         mydb.close();

			break;
		case 5:
			this.finish();
			break;

		}
		return super.onOptionsItemSelected(item);
	}

}