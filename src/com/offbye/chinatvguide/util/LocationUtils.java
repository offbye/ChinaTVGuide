
package com.offbye.chinatvguide.util;

import com.offbye.chinatvguide.PreferencesActivity;
import com.offbye.chinatvguide.server.user.UserInfoActivity;
import com.offbye.chinatvguide.server.user.UserStore;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;
import java.util.Locale;

public class LocationUtils {

    private static final String TAG = "LocationUtils";

    public static void getLocation(Context context) {
        try {
            Log.d(TAG, "getLocation");
            LocationManager locMan = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
            Location location = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                double latitude = location.getLatitude();// 获取纬度
                double longitude = location.getLongitude();// 获取经度
                Geocoder gc = new Geocoder(context, Locale.CHINA);

                List<Address> listAddress = gc.getFromLocation(latitude, longitude, 1);
                if (listAddress.size() > 0) {
                    Address a = listAddress.get(0);

                    String address = a.getAdminArea() +  a.getLocality() + a.getThoroughfare();
                    Log.d(TAG, "" + address + a.getAdminArea() + a.getSubAdminArea());
                    UserStore.setLocation(context, address.replaceAll("null", ""));

                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    prefs.edit().putString(PreferencesActivity.KEY_PROVINCE, a.getAdminArea())
                            .commit();
                    prefs.edit().putString(PreferencesActivity.KEY_CITY, a.getLocality()).commit();
                }
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }
}
