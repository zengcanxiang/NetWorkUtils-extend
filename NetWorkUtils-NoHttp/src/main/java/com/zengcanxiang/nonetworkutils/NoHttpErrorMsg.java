package com.zengcanxiang.nonetworkutils;

import com.yolanda.nohttp.error.ArgumentError;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.StorageReadWriteError;
import com.yolanda.nohttp.error.StorageSpaceNotEnoughError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;
import com.zengcanxiang.network.NetWorkError;

/**
 * Created by Administrator on 2016/3/5.
 */
public class NoHttpErrorMsg extends NetWorkError {

    private int what;
    private String url;
    private Object tag;
    private Exception exception;
    private int responseCode;
    private long networkMillis;

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getNetworkMillis() {
        return networkMillis;
    }

    public void setNetworkMillis(long networkMillis) {
        this.networkMillis = networkMillis;
    }

    public String decideError(){
        StringBuffer message=new StringBuffer();
        if (exception instanceof ClientError) {
            message.append("客户端错误");
        } else if (exception instanceof ServerError) {
            message .append( "服务器错误");
        } else if (exception instanceof NetworkError) {
            message .append( "网络不可用，请检查网络");
        } else if (exception instanceof StorageReadWriteError) {
            message .append( "存储卡错误，请检查存储卡");
        } else if (exception instanceof StorageSpaceNotEnoughError) {
            message .append( "存储位置空间不足");
        } else if (exception instanceof TimeoutError) {
            message .append( "请求超时");
        } else if (exception instanceof UnKnownHostError) {
            message .append( "找不到服务器");
        } else if (exception instanceof URLError) {
            message .append( "url地址错误");
        } else if (exception instanceof ArgumentError) {
            message .append( "请求参数错误");
        } else {
            message .append( "未知错误");
        }
        return message.toString();
    }
}
