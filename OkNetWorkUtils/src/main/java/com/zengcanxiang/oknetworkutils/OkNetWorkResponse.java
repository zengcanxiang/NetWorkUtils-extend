package com.zengcanxiang.oknetworkutils;

import com.alibaba.fastjson.JSONObject;
import com.zengcanxiang.network.NetWorkResponse;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Administrator on 2016/3/7.
 */
public class OkNetWorkResponse<T> extends NetWorkResponse<T> {

    private Response okHttpResponse;

    public OkNetWorkResponse(Response okHttpResponse) {
        this.okHttpResponse = okHttpResponse;
    }

    @Override
    public String getString() {

        String msg = null;
        try {
            msg = okHttpResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Override
    public T getObj(Class<T> cls) {
        T obj = JSONObject.parseObject(getString(), cls);
        return obj;
    }

    @Override
    public Object getResponse() {
        return okHttpResponse;
    }
}
