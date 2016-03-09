package com.zengcanxiang.nonetworkutils;

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
}
