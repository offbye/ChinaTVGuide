package com.offbye.chinatvguide;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public final class PreferencesActivity extends PreferenceActivity
    implements OnSharedPreferenceChangeListener {

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

  // Prevent the user from turning off both decode options
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//    if (key.equals(KEY_ALARM)) {
//      alarmTimes.setEnabled(alarm.isChecked());
//      alarmTimes.setChecked(true);
//    } else if (key.equals(KEY_ALARM_TIMES)) {
//      alarm.setEnabled(alarmTimes.isChecked());
//      alarm.setChecked(true);
//    }
  }
}
