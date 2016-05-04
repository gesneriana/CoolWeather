package com.coolweather.app.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016-05-03.
 * 应用程序启动的时候启动,应用程序结束的时候结束
 */
public class MyApplication extends Application {

    private static Context context;

    /**
     * 应用程序启动的时候创建一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    /**
     * 每个活动包含一个context,每个服务也包含一个contex
     * @return  返回应用程序全局范围内的context,也是所有活动和服务以及广播都可以共用的context
     */
    public static Context getContext(){
        return context;
    }
}
