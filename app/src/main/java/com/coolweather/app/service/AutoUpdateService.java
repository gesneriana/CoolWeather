package com.coolweather.app.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.receiver.AutoUpdateReceiver;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

/**
 * Created by Administrator on 2016-05-06.
 */
public class AutoUpdateService extends Service {

    /**
     * 在活动和服务进行绑定的时候会执行,可以与多个活动绑定
     * 绑定之后返回一个 IBinder对象 以便活动和服务间通信
     * @param intent    用于启动绑定服务的 Intent
     * @return      一个继承 Binder 类并实现各种功能的类对象, 一般此类是服务类的 内部类
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 在服务启动的时候会执行,可以执行多次
     * 停止服务必须同时解绑服务
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("调试信息","服务正在执行...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        /**
         * 启动一个定时执行的任务,并且唤醒CPU
         */
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=8*60*60*1000;    // 8小时毫秒数
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;   // 开机到现在所经过的时间+8小时

        // 注意,这里第二个参数必须设置为 广播接收器
        Intent i=new Intent(this,AutoUpdateReceiver.class);  // 设置为启动广播.如果设置为自己将不会有任何效果
        PendingIntent pi=PendingIntent.getBroadcast(this,0,i,0);    // 获取一个能够执行广播的 PendingIntent
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);     // 设置8小时之后能够唤醒CPU
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 更新天气信息,并将更新数据保存到本地的xml文件中
     */
    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        // 从本地的缓存数据中读取天气代码
        String weatherCode=prefs.getString("weather_code","");
        if(TextUtils.isEmpty(weatherCode)){
            Log.d("调试信息","没有从本地缓存数据获取到当前的天气代码");
            return;     // 中断执行,不会更新本地缓存数据
        }
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        // 根据 服务器地址和 天气代码 发送请求
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
