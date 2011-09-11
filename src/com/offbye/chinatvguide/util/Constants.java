package com.offbye.chinatvguide.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
	public static String key = "wztv20110911";
	public static final int VERSION_CODE = 18;
	public static final String URL_UPDATE = "http://wztv.sinaapp.com/client/update.php";
	
	public static String url_tvrate = "http://wztv.sinaapp.com/ga/tvrate.php";
	public static String url_tvrecommend = "http://wztv.sinaapp.com/ga/tvrecommend.php";

	public static String url_tvs = "http://wztv.sinaapp.com/client/tvs.php";
	public static String url_local = "http://wztv.sinaapp.com/client/local.php";
	public static String url_broadcast = "http://wztv.sinaapp.com/client/tvbroadcast.php";
	public static String url_sync = "http://wztv.sinaapp.com/client/tvsync.php";
	public static String url_shuzi = "http://wztv.sinaapp.com/client/tvshuzi.php";

	public static String CONSUMER_KEY = "1670092448";
	public static String CONSUMER_SECRET = "04a3a910a2cd3012d37426aee0364466";
	
	public static final String URL_SUGGEST = "http://wztv.sinaapp.com/client/suggest.php";

	public static final String URL_COMMENT = "http://wztv.sinaapp.com/client/comment.php";
	public static final String URL_USERINFO = "http://wztv.sinaapp.com/client/userinfo.php";
	public static final String URL_USER_REG = "http://wztv.sinaapp.com/client/user_reg.php?email=%s&password=%s&screenname=%s";
	public static final String URL_USER_LOGIN = "http://wztv.sinaapp.com/client/user_login.php?email=%s&password=%s";
	public static final String URL_COMMENT_LIST = "http://wztv.sinaapp.com/client/comment_list.php";
	
	public static final String URL_TVS = "url_tvs";
	public static final String PREFS_CONST = "const";
	public static final String URL_LOCAL = "url_local";
	public static final String URL_BROADCAST = "url_broadcast";
	public static final String URL_SYNC = "url_sync";
	public static final String URL_SHUZI = "url_shuzi";
	
	
	public static String getUrlTvs(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        return sp.getString(URL_TVS, url_tvs);
    }

    public static void setUrlTvs(Context context,String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        sp.edit().putString(URL_TVS, url).commit();
    }
    
    public static String getUrlLocal(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        return sp.getString(URL_LOCAL, url_local);
    }

    public static void setUrlLocal(Context context,String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        sp.edit().putString(URL_LOCAL, url).commit();
    }

    public static String getUrlBroadcast(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        return sp.getString(URL_BROADCAST, url_broadcast);
    }

    public static void setUrlBroadcast(Context context,String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        sp.edit().putString(URL_BROADCAST, url).commit();
    }

    
    public static String getUrlSync(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        return sp.getString(URL_SYNC, url_sync);
    }

    public static void setUrlSync(Context context,String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        sp.edit().putString(URL_SYNC, url).commit();
    }

    
    public static String getUrlShuzi(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        return sp.getString(URL_SHUZI, url_shuzi);
    }

    public static void setUrlShuzi(Context context,String url) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_CONST, 0);
        sp.edit().putString(URL_SHUZI, url).commit();
    }
}
