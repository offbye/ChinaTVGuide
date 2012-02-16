
package com.offbye.chinatvguide.server.media;

import com.offbye.chinatvguide.util.AppException;
import com.offbye.chinatvguide.util.Base64;
import com.offbye.chinatvguide.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArraySet;

public class MediaStore {
    private static MediaStore sInstance;

    private static CopyOnWriteArraySet<String> channels = new CopyOnWriteArraySet<String>();

    private static ArrayList<Media> ml = new ArrayList<Media>();

    private MediaStore() {
    }

    public synchronized static MediaStore getInstance() {
        if (sInstance == null) {
            sInstance = new MediaStore();
        }
        return sInstance;
    }

    public boolean requestMediaList(Context context) {
        String sb;
        try {
            sb = HttpUtil.getUrl(context, "http://m.intotv.net/sc/media.php");
            if (sb.length() > 0 && !sb.toString().equals("null") && !sb.toString().equals("error")) {
                JSONArray ja = new JSONArray(new String(Base64.decode(sb.toString(),Base64.DEFAULT),"UTF-8"));
                int len = ja.length();
                for (int i = 0; i < len; i++) {
                    JSONObject jp = ja.getJSONObject(i);
                    Media m = new Media();
                    m.setId(jp.getString("id"));
                    m.setTitle(jp.getString("title"));
                    m.setHref(jp.getString("href"));
                    m.setChannel(jp.getString("channel"));
                    m.setBitrate(jp.getString("bitrate"));
                    ml.add(m);
                    channels.add(jp.getString("channel").trim());
                    //Log.d("media", m.getHref());
                }
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public ArrayList<Media> getMediaList(String channel){
        if(null == channel || "".equals(channel.trim())){
            return (ArrayList<Media>) Collections.unmodifiableList(ml);
        }
        else{
            ArrayList<Media>  temp = new  ArrayList<Media>(); 
            for (Media m : ml){
                if(m.getTitle().equals(channel)){
                    temp.add(m);
                }
            }
            return temp;
        }
    }
    
    public boolean isInMediaList(String channel){
        return channels.contains(channel);
    }
    
    public ArrayList<String> getHrefs(String channel){
        ArrayList<String>  temp = new  ArrayList<String>(); 
        for (Media m : ml){
            if(m.getChannel().equals(channel)){
                temp.add(m.getHref());
            }
        }
        return temp;
    }
    
    public ArrayList<String> getTitles(String channel){
        ArrayList<String>  temp = new  ArrayList<String>(); 
        for (Media m : ml){
            if(m.getChannel().equals(channel)){
                temp.add(m.getChannel() + "," + m.getBitrate());
            }
        }
        return temp;
    }
}
