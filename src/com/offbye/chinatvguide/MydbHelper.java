package com.offbye.chinatvguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MydbHelper {
    private static final String TAG = "MydbHelper";
    private static final String DATABASE_NAME = "chinatvguide6.db";  
    SQLiteDatabase db;
    Context context;
    
    public MydbHelper(Context _context) {
        context=_context;
        db=context.openOrCreateDatabase(DATABASE_NAME, 0, null);   //创建数据库
        //Log.v(TAG,"db path="+db.getPath());
    }
    
    public void CreateTable_tvchannel() {
        try{
        	db.execSQL("DROP TABLE IF EXISTS tvchannel");
            db.execSQL("CREATE TABLE tvchannel(" //创建问题表
                    + "_id INTEGER PRIMARY KEY,"
                    + "channel TEXT,"
                    + "channelname TEXT,"
                    + "image TEXT,"
                    + "hidden integer,"
                    + "type TEXT"
                    + ");");
            //Log.v(TAG,"Create Table tvchannel ok");
        }catch(Exception e){
            //Log.v(TAG,"Create Table tvchannel err,table exists.");
        }
    }
    
    public void CreateTable_tvprogram() {
        try{
        	db.execSQL("DROP TABLE IF EXISTS tvprogram");
            db.execSQL("CREATE TABLE tvprogram ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "channel TEXT,"
                    + "date TEXT,"
                    + "starttime TEXT,"
                    + "endtime TEXT,"
                    + "program TEXT,"
                    + "daynight TEXT,"
                    + "channelname TEXT"
                    + ");");
            //Log.v(TAG,"Create Table tvprogram ok");
        }catch(Exception e){
            //Log.v(TAG,"Create Table tvprogram err,table exists.");
        }
    }
    public void CreateTable_localprogram() {
        try{
        	db.execSQL("DROP TABLE IF EXISTS localprogram");
            db.execSQL("CREATE TABLE localprogram ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "channel TEXT,"
                    + "date TEXT,"
                    + "starttime TEXT,"
                    + "endtime TEXT,"
                    + "program TEXT,"
                    + "daynight TEXT,"
                    + "channelname TEXT"
                    + ");");
            //Log.v(TAG,"Create Table tvprogram ok");
        }catch(Exception e){
            //Log.v(TAG,"Create Table tvprogram err,table exists.");
        }
    }
    
    public void CreateTable_favouriteprogram() {
        try{
        	db.execSQL("DROP TABLE IF EXISTS favouriteprogram");
            db.execSQL("CREATE TABLE favouriteprogram ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "channel TEXT,"
                    + "date TEXT,"
                    + "starttime TEXT,"
                    + "endtime TEXT,"
                    + "program TEXT,"
                    + "daynight TEXT,"
                    + "channelname TEXT"
                    + ");");
            //Log.v(TAG,"Create Table favouriteprogram ok");
        }catch(Exception e){
            //Log.v(TAG,"Create Table favouriteprogram err,table exists.");
        }
    }
    
    public void CreateTable_location() {
        try{
        	db.execSQL("DROP TABLE IF EXISTS location");
        	db.execSQL("CREATE TABLE location ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "province TEXT,"
                    + "city TEXT,"
                    + "address TEXT,"
                    + "createtime TIMESTAMP"
                    + ");");
            //Log.v(TAG,"Create Table location ok");
        }catch(Exception e){
            //Log.v(TAG,"Create Table location err,table exists.");
        }
    }
    public void CreateTable_rsschannel() {
        try{
        	db.execSQL("DROP TABLE IF EXISTS rsschannel");
            db.execSQL("CREATE TABLE rsschannel("
                    + "_id INTEGER PRIMARY KEY,"
                    + "url TEXT,"
                    + "title TEXT,"
                    + "image TEXT,"
                    + "hidden integer,"
                    + "item_count integer,"
                    + "description_count integer,"
                    + "category TEXT"
                    + ");");
        }catch(Exception e){
            //Log.v(TAG,"Create Table tvchannel err,table exists.");
        }
    }
    
    public void CreateTable_v5() {
        try{
            db.execSQL("CREATE TABLE v5 ("
                    + "_id INTEGER PRIMARY KEY,"
                    + "vertion TEXT,"
                    + "createtime TIMESTAMP"
                    + ");");
            //Log.v(TAG,"Create Table v3 ok");
        }catch(Exception e){
            //Log.v(TAG,"Create Table v3 err,table exists.");
        }
    }
    
    public void InitAcctitem() {
        
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv1','CCTV1综合频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv2','CCTV2经济频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv3','CCTV3综艺频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv4','CCTV4中文国际频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv5','CCTV5体育频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv6','CCTV6电影频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv7','CCTV7少儿·军事·农业','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv8','CCTV8电视剧频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv9','CCTV9英语国际频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv10','CCTV10科教频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv11','CCTV11戏曲频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv12','CCTV12社会与法频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvxwpd','CCTV新闻频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvsepd','CCTV少儿频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv_e','CCTV-E西班牙语频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv_f','CCTV-F法语频道','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv-gq','CCTV高清频道','',0,'cctv')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jsws','江苏卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ahws','安徽卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('btv1','北京卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hunanws','湖南卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hubeiws','湖北卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('zqws','重庆卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('tjws','天津卫视','',0,'sjws')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jxws','江西卫视','',0,'sjws')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('zjws','浙江卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('dfaws','东方卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sdws','山东卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sx1ws','山西卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sx3ws','陕西卫视','',0,'sjws')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('dnws','东南卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('lyws','旅游卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('gdws','广东卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('gxws','广西卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('szws','深圳卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('nfwspd','南方卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('scws','四川卫视','',0,'sjws')");
        
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('gzws','贵州卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ynws','云南卫视','',0,'sjws')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('lnws','辽宁卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jlws','吉林卫视','',0,'sjws')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hljws','黑龙江卫视','',0,'sjws')");

        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hnws','河南卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hbws','河北卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('nxws','宁夏卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('gsws','甘肃卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('qhws','青海卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('xzhyws','西藏汉语卫视','',0,'sjws')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('xjws','新疆卫视','',0,'sjws')");
 
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('btv10','卡酷动画','',0,'sjws')");

        //教育台
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jyyt','教育一台','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jyet','教育二台','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jyst','教育三台','',0,'cctv')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cetvkzkt','CETV空中课堂','',0,'cctv')");

        //港澳台
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fhzw','凤凰中文','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fhdy','凤凰电影','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fhzx','凤凰资讯','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ygws','阳光卫视','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('xkws','星空卫视','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('dfwsyz','东风卫视(亚洲)','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hyws','华娱卫视','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('tspd','探索频道','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ayws','澳亚卫视','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('bgt','亚视-本港台','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ys-gjt','亚视-国际台','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fct','TVB-翡翠台','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('mzt','TVB-明珠台','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('tvb-xhpd','TVB-星河频道','',0,'gt')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('tvb8','TVB8','',0,'gt')");

        //shuzi tv
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('zqzx','证券资讯','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('zhms','中华美食','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvhjjc','怀旧剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvxdnx','现代女性','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctv-dy','CCTV-电影','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvsypd','摄影频道','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('xdm','新动漫','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvly','CCTV-梨园','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvcmzx','彩民在线','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvlbf','CCTV老年福','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvdsgw','电视购物','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvgfjs','国防军事','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvxfjl','先锋纪录','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvdsnxss','央视女性时尚','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvzqjy','央视早期教育','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvqx','CCTV气象','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('dyjc','第一剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('dszn','电视指南','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('flfw','法律服务','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fyjc','风云剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fyyl','风云音乐','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('fyzq','风云足球','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('gef','高尔夫','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('gefwq','高尔夫网球','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jz','靓妆','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('lxsj','留学世界','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('qm','汽摩','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('qnxy','青年学苑','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sjdl','世界地理','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('tywq','天元围棋','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ysjp','央视精品','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ysxq','央视戏曲','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ysyl','央视娱乐','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('yxjj','游戏竞技','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('yyzn','孕育指南','',0,'sz')");
        
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvdfcj','SiTV东方财经','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvmstf','SiTV美食天府','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvdsjc','SiTV都市剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvjdjc','SiTV经典剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvwjyz','SiTV玩具益智','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvwsjk','SiTV卫生健康','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvyxfy','SiTV游戏风云','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvjbzq','SiTV劲爆足球','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvbjjc','SiTV白金剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvsyjc','SiTV首映剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvjspd','SiTV金色频道','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvxxks','SiTV学习考试','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvdmxc','SiTV动漫秀场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvyyjs','SiTV英语教室','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvqjs','SiTV全纪实','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvjsqc','SiTV极速汽车','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvwxty','SiTV五星体育','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvxfc','SiTV幸福彩','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvqcxj','SiTV七彩戏剧','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvshss','SiTV生活时尚','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvjbty','SiTV劲爆体育','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvhxjc','SiTV欢笑剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvmlyy','SiTV魅力音乐','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvzxqx','SiTV资讯气象','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvshss(sh)','SiTV生活时尚(上海)','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvylqx','SiTV娱乐前线','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvwssj','SiTV武术世界','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('sitvfztd','SITV法治天地','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('xyl','新娱乐','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('hqly','环球旅游','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('jsjc','京视剧场','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('bgcmzypd','置业频道','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('dgyy','动感音乐','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ajgw','爱家购物','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ytcq','弈坛春秋','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cm','车迷','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('kszx','考试在线','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('qqbb','亲亲宝贝','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('shdy','四海钓鱼','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('ozzqpd','欧洲足球频道','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('xfpy','先锋乒羽','',0,'sz')");
        db.execSQL("insert into tvchannel(channel,channelname,image,hidden,type) values ('cctvxfjl','先锋纪录','',0,'sz')");
        
        
        //预置RSS初始化
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://ent.qq.com/tv/rss_tv.xml','腾讯娱乐-电视','最贴近用户群体的专业网络电视资讯门户',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://ent.qq.com/movie/rss_movie.xml','腾讯娱乐-电影','国内最具人气的专业电影资讯网站',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://ent.qq.com/newxw/rss_start.xml','腾讯娱乐-明星','最具有八卦精神的网络媒体，为网友提供最劲爆的明星资讯，对娱乐圈重大事件进行深入的专题报道',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://ent.qq.com/m_news/rss_yinyue.xml','腾讯娱乐-音乐','拥有海量、正版的歌曲视听、专辑推荐评价、紧跟流行音乐热点',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://ent.qq.com/entpic/rss_entpic.xml','腾讯娱乐-图片站','汇聚八卦、事件、现场、唯美、走光等当日图片',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://ent.qq.com/chatroom/chatnews/rss_starchat.xml','腾讯娱乐-名人坊','是最著名的网络明星视频访谈栏目之一',0,20,300,'tv')");

        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/ent/hot_roll.xml','娱乐要闻汇总-新浪娱乐','新浪网娱乐报道之娱乐要闻汇总',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/news/allnews/ent.xml','焦点新闻-新浪娱乐','新浪网娱乐频道焦点新闻',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/ent/tv/focus7.xml','电视前沿-新浪娱乐','乐新浪网电视前沿报道，资讯分析',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/ent/film/focus7.xml','电影宝库-新浪娱乐','电影全接触，多方位采撷精彩影讯',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/ent/star/focus7.xml','明星全接触-新浪娱乐','深入了解星讯，明星全接触',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/ent/inner.xml','内地明星-新浪娱乐','深入了解内地明星资讯，内地明星全接触',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.sina.com.cn/ent/hongkong.xml','港台明星-新浪娱乐','深入了解日韩明星资讯，日韩明星全接触',0,20,300,'tv')");

        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.yule.sohu.com/rss/yuletoutiao.xml','搜狐娱乐头条','最权威、最快速、最全面！为人民娱乐',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.yule.sohu.com/rss/redianpinglun.xml','搜狐热点评论','以独特的视角看纷纷扰扰的娱乐圈',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.yule.sohu.com/rss/yulequan.xml','搜狐娱乐圈','深入娱乐圈，以最快速的反应为您全面报道娱乐圈里的大事小情',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.yule.sohu.com/rss/dianshiqianyan.xml','搜狐电视前沿','集电视节目推荐、介绍明星动态、传递最新影视动态、业界信息发布及网友互动交流等为一体的综合电视频道',0,20,300,'tv')");
        db.execSQL("insert into rsschannel(url,title,image,hidden,item_count,description_count,category) values ('http://rss.yule.sohu.com/rss/dianying.xml','搜狐电影频道','实时关注影视动态，及时报道影视资讯',0,20,300,'tv')");

        //Log.v(TAG,"insert into ok");
        
    }
    
    
    public void FirstStart(){
      //如果是第一次启动,就不存在tvchannel这张表.
    	Cursor c = null;
		try {
			String col[] = { "type", "name" };
			c = db.query("sqlite_master", col, "name='v5'", null, null, null,
					null);
			if (c.getCount() == 0) {
				CreateTable_tvchannel();
				CreateTable_tvprogram();
				CreateTable_localprogram();
				CreateTable_location();
				CreateTable_v5();
				CreateTable_favouriteprogram();
				CreateTable_rsschannel();
				InitAcctitem();
			}
		} catch (Exception e) {
			Log.e(TAG, "e=" + e.getMessage());
		} finally {
			if (null != c) {
				c.close();
			}
		}
        
    }   
    
    public void close(){
        db.close();
    }
    
    public Cursor fetchChannel(long id) throws SQLException {
        Cursor mCursor =
                db.query("tvchannel", new String[] {"_id",
                		"channel","channelname"},  "_id=" + id, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            //Log.v(TAG,"mCursor.moveToFirst();"+mCursor.getCount());
        }
        return mCursor;

    }
    public Cursor fetchChannel(String channel) throws SQLException {
        Cursor mCursor =
                db.query("tvchannel", new String[] {"_id",
                		"channel","channelname"},  "channel='" + channel+"'", null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
            //Log.v(TAG,"mCursor.moveToFirst();"+mCursor.getCount());
        }
        return mCursor;

    }
    public Cursor fetchChannels() throws SQLException {
        Cursor mCursor =
                db.query(true, "tvchannel", new String[] {"_id",
                        "channel", "channelname", "image","hidden", "type"},  "", null,
                        null, null, null, null);
        return mCursor;
    }
    public Cursor fetchChannels(String type) throws SQLException {
        Cursor mCursor =
                db.query(true, "tvchannel", new String[] {"_id",
                        "channel", "channelname", "image","hidden", "type"},  "type='" + type+"'", null,
                        null, null, null, null);
        return mCursor;
    }
    public Cursor fetchChannels(int fav) throws SQLException {
        Cursor mCursor =
                db.query(true, "tvchannel", new String[] {"_id",
                        "channel", "channelname", "image","hidden", "type"},  "hidden=" +fav, null,
                        null, null, null, null);
        return mCursor;
    }
    public void  toggleFavouriteChannel(String  channel,String hidden) throws SQLException {
    	if("1".equals(hidden)){
    		db.execSQL("update tvchannel set hidden=0 where channel ='"+channel+"'" );
    	}
    	else{
    		db.execSQL("update tvchannel set hidden=1 where channel ='"+channel+"'" );
    	}
    }
    public String getFavs(){
    	StringBuilder sb = new StringBuilder();
    	Cursor mCursor =
            db.query(true, "tvchannel", new String[] {"_id",
                    "channel", "channelname", "image","hidden", "type"},  "hidden=1", null,
                    null, null, null, null);
    	if(null != mCursor ){
    		try{
    			while(mCursor.moveToNext()){
    				sb.append("'");
    				sb.append(mCursor.getString(1));
    				sb.append("'");
    				if(!mCursor.isLast()){
    					sb.append(",");
    				}
    			}
    		}
    		finally{
    			mCursor.close();
    		}
    	}
    	return sb.toString();
    }
    
    public Cursor fetchRSSChannels() throws SQLException {
        Cursor mCursor =
                db.query(true, "rsschannel", new String[] {"_id",
                        "url", "title", "image","hidden","item_count","description_count", "category"},  "", null,
                        null, null, null, null);
        return mCursor;
    }
    
    public long insertProgram(String channel, String date,String starttime,String endtime,String program,String daynight,String channelname) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("channel", channel);
        initialValues.put("date", date);
        initialValues.put("starttime", starttime);
        initialValues.put("endtime", endtime);
        initialValues.put("program", program);
        initialValues.put("daynight", daynight);
        initialValues.put("channelname", channelname);
        return db.insert("tvprogram", null, initialValues);
    }
    public long addFavoriteProgram(String channel, String date,String starttime,String endtime,String program,String daynight,String channelname) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("channel", channel);
        initialValues.put("date", date);
        initialValues.put("starttime", starttime);
        initialValues.put("endtime", endtime);
        initialValues.put("program", program);
        initialValues.put("daynight", daynight);
        initialValues.put("channelname", channelname);
        return db.insert("favouriteprogram", null, initialValues);
    }
    public boolean deleteAllFavoritePrograms(){
        return db.delete("favouriteprogram", "1=1",null)>0;
    }
    public long insertLocalProgram(String channel, String date,String starttime,String endtime,String program,String daynight,String channelname) {
        ContentValues initialValues = new ContentValues();
        initialValues.put("channel", channel);
        initialValues.put("date", date);
        initialValues.put("starttime", starttime);
        initialValues.put("endtime", endtime);
        initialValues.put("program", program);
        initialValues.put("daynight", daynight);
        initialValues.put("channelname", channelname);
        return db.insert("localprogram", null, initialValues);
    }
    public Cursor getFavoritePrograms() throws SQLException {
        Cursor mCursor =
                db.query(true, "favouriteprogram", new String[] {"_id",
                        "channel", "date", "starttime","endtime","program", "daynight","channelname"}, null, null,
                        null, null, null, null);
        return mCursor;
    }

    public Cursor getProgramsByDate(String date) throws SQLException {
        Cursor mCursor =
                db.query(true, "tvprogram", new String[] {"_id",
                        "channel", "date", "starttime","endtime","program", "daynight","channelname"}, "date='"+date+"'", null,
                        null, null, null, null);
        return mCursor;
    }
    
    public Cursor searchPrograms(String selection) throws SQLException {
        Cursor mCursor = db.query(true, "tvprogram", new String[] {"_id",
                        "channel", "date", "starttime","endtime","program", "daynight","channelname"}, selection, null,
                        null, null, null, null);
        return mCursor;
    }
    public Cursor searchLocalPrograms(String selection) throws SQLException {
        Cursor mCursor = db.query(true, "localprogram", new String[] {"_id",
                        "channel", "date", "starttime","endtime","program", "daynight","channelname"}, selection, null,
                        null, null, null, null);
        return mCursor;
    }    
    public int getProgramsCountByDate(String date) throws SQLException {
    	int count=0;
        Cursor mCursor = db.rawQuery("select count(*) counts from tvprogram where date='"+date+"'",null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        count = mCursor.getInt(0);
        mCursor.close();
        return count;
    }
    
    public boolean deleteAllPrograms(){
               return db.delete("tvprogram", "1=1",null)>0;
    }
}
