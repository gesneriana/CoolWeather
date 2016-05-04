package com.coolweather.app.util;

/**
 * 主要用于请求服务器数据的异步消息处理机制
 * 以实现在子线程中向主进程中传递数据
 * Created by Administrator on 2016-05-03.
 * 请求网络数据的回调方法接口
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
