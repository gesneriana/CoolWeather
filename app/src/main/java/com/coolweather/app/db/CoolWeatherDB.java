package com.coolweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-05-03.
 * 操作数据库的工具类,从数据库加载省份等信息,或者向数据库保存省份等信息
 */
public class CoolWeatherDB {

    /**
     * 数据库名称
     */
    public static final String DB_NAME="cool_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION=1;

    /**
     * 当前类静态对象
     */
    private static CoolWeatherDB coolWeatherDB;

    /**
     * 数据库执行增删改查操作的类
     */
    private SQLiteDatabase db;

    /**
     * 私有化的构造方法,防止多次创建
     * @param context
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db=dbHelper.getWritableDatabase();
    }

    /**
     * 同步的方法获取当前类的静态的实例
     * @param context
     * @return
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB==null){
            coolWeatherDB=new CoolWeatherDB(context);
        }
        return coolWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     * @param province
     */
    public void saveProvince(Province province){
        if(province!=null){
            ContentValues values=new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }

    /**
     * 从数据库加载所有省份的 Province 对象的 list集合
     * @return
     */
    public List<Province> loadProvinces(){
        List<Province> list=new ArrayList<>();
        Cursor cursor=db.query("Province",null,null,null,null,null,null);
        while (cursor.moveToNext()){
            Province province=new Province();
            province.setId(cursor.getInt(cursor.getColumnIndex("id")));
            province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
            province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            list.add(province);
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将city对象存到数据库
     * @param city
     */
    public void saveCity(City city){
        if(city!=null){
            ContentValues values=new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            db.insert("City",null,values);
        }
    }

    /**
     * 从数据库读取某省份的所有城市信息
     * @param provinceId    省份编号,Province表自增长的标识列,外键
     * @return
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)}, null, null, null);
        while (cursor.moveToNext()){
            City city=new City();
            city.setId(cursor.getInt(cursor.getColumnIndex("id")));
            city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceId(provinceId);
            list.add(city);
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }

    /**
     * 将County实例保存到数据库
     * @param county
     */
    public void saveCounty(County county){
        if(county!=null){
            ContentValues values=new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());       // 城市编号,外键,City表自增长的标识列
            db.insert("County",null,values);
        }
    }

    /**
     * 从数据库读取某城市下的所有区县的信息
     * @param cityId    城市编号,外键,City表的自增长的主键列 编号
     * @return
     */
    public List<County> loadCounties(int cityId){
        List<County> list=new ArrayList<>();
        Cursor cursor=db.query("County",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        while (cursor.moveToNext()){
            County county=new County();
            county.setId(cursor.getInt(cursor.getColumnIndex("id")));
            county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
            county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
            county.setCityId(cityId);
            list.add(county);
        }
        if(cursor!=null){
            cursor.close();
        }
        return list;
    }
}
