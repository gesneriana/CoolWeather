package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016-05-03.
 * 用于解析服务器返回的特殊的数据
 * 并且保存到数据库中对应的表中
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据,保存到数据库
     * 将 01|北京,02|上海,03|天津 格式的数据拆分
     * @param coolWeatherDB 操作数据库的工具类,执行增删改查等操作
     * @param response      服务器响应的省份数据
     * @return true, 表示保存成功     false,表示保存失败
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    // p代表的是一个省份的信息,例如 02|上海
                    Log.d("debug", "当前省份信息p的值为: " + p);
                    String[] array = p.split("\\|");    // java中字符 | 前面必须加上 \\  然而C#中却不用 莫名其妙的问题
                    Province province = new Province();
                    Log.d("debug", "字符串数组值: 省份代码: " + array[0] + " 省份名称: " + array[1]);
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    // 将解析出来的数据保存到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的城市数据,保存到数据库
     *
     * @param coolWeatherDB 操作数据库的工具类,执行增删改查等操作
     * @param response      服务器响应的城市数据
     * @param provinceId    省份编号,外键,Province表的主键列,自增长的标识列
     * @return true, 表示保存成功     false,表示保存失败
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    // c代表的是一个城市的信息,例如 1904|苏州
                    Log.d("debug", "当前城市信息c的值为: " + c);
                    String[] array = c.split("\\|");    // java中字符 | 前面必须加上 \\  然而C#中却不用 莫名其妙的问题
                    City city = new City();
                    Log.d("debug", "字符串数组值: 城市代码: " + array[0] + " 城市名称: " + array[1]);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    // 将解析出来的城市数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的区县级数据,保存到数据库
     *
     * @param coolWeatherDB 操作数据库的工具类,执行增删改查等操作
     * @param response      服务器响应的区县数据
     * @param cityId        城市编号,外键,City表的主键列,自增长的标识列
     * @return true, 表示保存成功     false,表示保存失败
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    // c代表的是一个区县的信息,例如 190404|昆山
                    Log.d("debug", "当前城市区县c的值为: " + c);

                    String[] array = c.split("\\|");    // java中字符 | 前面必须加上 \\  然而C#中却不用 莫名其妙的问题
                    County county = new County();
                    Log.d("debug", "字符串数组值: 区县代码: " + array[0] + " 区县名称: " + array[1]);
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON格式区县天气信息数据,并将解析的数据保存到本地
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context,String response){
        try{
            Log.d("调试信息",response);
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的天气信息存储到SharedPreferences文件中
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    public static void saveWeatherInfo(Context context, String cityName, String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
