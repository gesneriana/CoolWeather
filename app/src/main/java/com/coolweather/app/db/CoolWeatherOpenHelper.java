package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016-05-03.
 * Sqlite数据库工具类,打开与创建,升级数据库
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * Province表创建语句
     */
    public static final String CREATE_PROVINCE="create table Province (id integer primary key autoincrement, province_name text, province_code text)";

    /**
     * City表创建语句
     */
    public static final String CREATE_CITY="create table City (id integer primary key autoincrement, city_name text, city_code text, province_id integer)";

    /**
     * County表创建语句
     */
    public static final String CREATE_COUNTY="create table County (id integer primary key autoincrement, county_name text, county_code text, city_id integer)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    /**
     * 如果是第一次打开数据库,不存在数据库文件,则调用此方法
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);        // 创建Province表
        db.execSQL(CREATE_CITY);            // 创建City表
        db.execSQL(CREATE_COUNTY);          // 创建County表
    }

    /**
     * 数据库版本更新,将会自动调用此方法
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
