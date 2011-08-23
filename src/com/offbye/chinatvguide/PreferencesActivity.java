
package com.offbye.chinatvguide;

import com.offbye.chinatvguide.weibo.OAuthActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public final class PreferencesActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    static final String KEY_ALARM = "preferences_alarm";

    static final String KEY_ALARM_TIMES = "preferences_alarm_times";

    public static final String KEY_CITY = "preferences_city";

    public static final String KEY_PROVINCE = "preferences_province";

    static final String KEY_PLAY_BEEP = "preferences_play_beep";

    static final String KEY_VIBRATE = "preferences_vibrate";

    static final String KEY_HELP_VERSION_SHOWN = "preferences_help_version_shown";

    public static final String KEY_NOT_OUR_RESULTS_SHOWN = "preferences_not_out_results_shown";

    private CheckBoxPreference alarm;

    private CheckBoxPreference alarmTimes;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen preferences = getPreferenceScreen();
        preferences.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        alarm = (CheckBoxPreference) preferences.findPreference(KEY_ALARM);
        alarmTimes = (CheckBoxPreference) preferences.findPreference(KEY_ALARM_TIMES);
    }

    public static boolean isAutoSyncOn(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean("autosync", true);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // if (key.equals(KEY_ALARM)) {
        // alarmTimes.setEnabled(alarm.isChecked());
        // alarmTimes.setChecked(true);
        // } else if (key.equals(KEY_ALARM_TIMES)) {
        // alarm.setEnabled(alarmTimes.isChecked());
        // alarm.setChecked(true);
        // }
    }
    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference.getKey().equals("clearcache")){
            MydbHelper mydb =new MydbHelper(this);
            if(mydb.deleteAllPrograms()==true){
                Toast.makeText(this, R.string.program_data_deleted, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, R.string.no_data_deleted, Toast.LENGTH_LONG).show();
            }
            mydb.close();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
        
    }
}
