package com.offbye.chinatvguide.server.user;

import android.content.Context;
import android.content.SharedPreferences;

public class UserStore {
    public static final String PREFS_USER = "user";

    public static final String ACCESS_TPKEN = "AccessToken";

    public static final String ACCESS_TPKEN_SECRET = "AccessTokenSecret";

    public static final String USERID = "userid";

    public static final String SCREEN_NAME = "screenname";
    
    private static final String EMAIL = "email";

    private static final String POINT = "point";

    private static final String CHECKIN = "checkin";

    private static final String COMMENT = "comment";

    private static final String LOCATION = "location";

    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(USERID, "");
    }

    public static String getAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(ACCESS_TPKEN, "");
    }

    public static String getAccessSecret(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(ACCESS_TPKEN_SECRET, "");
    }


    public static String getScreenName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(SCREEN_NAME, "");
    }

    public static String getEmail(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(EMAIL, "");
    }

    public static String getLocation(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getString(LOCATION, "");
    }

    public static int getPoint(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getInt(POINT, 0);
    }

    public static int getCheckin(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getInt(CHECKIN, 0);
    }

    public static int getComment(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        return sp.getInt(COMMENT, 0);
    }
    
    public static void setScreenName(Context context,String name) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putString(SCREEN_NAME, name).commit();
    }
    
    public static void setEmail(Context context,String email) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putString(EMAIL, email).commit();
    }
    
    public static void setUserId(Context context,String userid) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putString(USERID, userid).commit();
    }

    public static void setLocation(Context context, String location) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putString(LOCATION, location).commit();
    }

    public static void setPoint(Context context, int point) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putInt(POINT, point).commit();
    }

    public static void setCheckin(Context context, int num) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putInt(CHECKIN, num).commit();
    }

    public static void setComment(Context context, int num) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_USER, 0);
        sp.edit().putInt(COMMENT, num).commit();
    }
}
