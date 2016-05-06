package com.coolweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.coolweather.app.service.AutoUpdateService;

/**
 * Created by Administrator on 2016-05-06.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    /**
     * 接受到广播的回调方法
     * @param context   发送广播的对象
     * @param intent    发送广播所需的参数 例如 Intent i=new Intent(this, AutoUpdateReceiver.class);
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, AutoUpdateService.class);
        context.startService(i);        // 服务发送了广播,广播接收器接受到广播之后,启动服务
    }
}
