package com.coolweather.app.util;

/**
 * 主要用于请求服务器数据的异步消息处理机制
 * 以实现在子线程中向主进程中传递数据
 * Created by Administrator on 2016-05-03.
 * 请求网络数据的回调方法接口
 */
public interface HttpCallbackListener {
    /**
     * 请求成功的回调方法
     * @param response  服务器响应的数据
     */
    void onFinish(String response);

    /**
     * 请求失败的回调方法
     * @param e     异常对象
     */
    void onError(Exception e);
}
