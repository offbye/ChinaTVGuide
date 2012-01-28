package com.offbye.chinatvguide.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    /**
     * 把日期转化为距离现在的时间
     * @param str1
     * @return 距离现在的时间
     */
    public static String getPassedTime(String str1) {
        String re = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        long day = 0;
        long hour = 0;
        long min = 0;
        try {
            one = df.parse(str1);
            long time1 = one.getTime();
            long time2 = new Date().getTime();
            long diff;
            
            if (one.getYear() != new Date().getYear()) {
                return str1; //if not same year , do not compute.
            }
            
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                //diff = time1 - time2;
                return str1;  //small than now not compute.
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (day > 2) {
            re = str1.substring(0, 10);
        } else {
            if (day > 0) {
                re = day + "天前";
            } else if (hour > 0) {
                re = hour + "小时前";
            } else if (min > 0) {
                re = min + "分钟前";
            } else {
                re = "刚刚";
            }
        }
        return re;
    }
    
    /**
     * format date "Wed, 25 Jan 2012 11:07:54 +0800" to "2012-01-25 11:07:54"
     * @param date "Wed, 25 Jan 2012 11:07:54 +0800"
     * @return
     */
    public static String formatDate(String date) {
        if (date.charAt(0) < 9) {
            return date;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        try {
            return sdfs.format(sdf.parse(date));
        } catch (ParseException ex) {
            return date;
        }
    }
    
    public static String getDateAndPassedTime(String date){
        String newDate = formatDate(date);
        String dDate = getPassedTime(newDate);
        if(dDate.length() > 6){
            return newDate;
        }
        else {
            return newDate + " " + dDate;  
        }
    }
}
