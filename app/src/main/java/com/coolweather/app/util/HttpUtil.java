package com.coolweather.app.util;

import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 主要用于请求服务器数据
 * Created by Administrator on 2016-05-03.
 * 请求网络数据的工具类,把一下通用的操作封装到静态方法中,
 * 并且开启了一个新线程,执行完成之后回调listener接口
 * 使用异步消息处理机制更新主进程UI
 */
public class HttpUtil {

    /**
     * 请求网络数据的通用方法,因为开启了线程 方法返回值没有任何意义
     * 所以使用了listener接口的回调方法 实现异步消息处理机制
     * @param address   网络连接地址
     * @param listener  请求网络数据成功后 监听器的匿名内部类实现的接口
     */
    public static void sendHttpRequest(final String address,@NonNull final HttpCallbackListener listener) {
        if(listener==null){
            Toast.makeText(MyApplication.getContext(),"监听器不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 开启线程
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    listener.onFinish(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError(e);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
