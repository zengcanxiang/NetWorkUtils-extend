package com.zengcanxiang.nonetworkutils;

import com.alibaba.fastjson.JSONObject;
import com.yolanda.nohttp.Response;
import com.zengcanxiang.network.NetWorkResponse;

/**
 * Created by Administrator on 2016/3/7.
 */
public class NoNetWorkResponse<T> extends NetWorkResponse<T> {

    private Response<T> noHttpResponse;

    public NoNetWorkResponse(Response<T> noHttpResponse){
        this.noHttpResponse=noHttpResponse;
    }

    @Override
    public String getString() {
        String msg = (String) noHttpResponse.get();

        return msg;
    }

    @Override
    public T getObj(Class<T> cls) {

        T obj = JSONObject.parseObject(getString(), cls);

        return obj;
    }

    @Override
    public Object getResponse() {
        return noHttpResponse;
    }

    public Response<T> getNoHttpResponse() {
        return noHttpResponse;
    }
}
