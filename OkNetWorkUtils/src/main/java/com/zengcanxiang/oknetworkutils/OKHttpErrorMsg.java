package com.zengcanxiang.oknetworkutils;

import com.zengcanxiang.network.NetWorkError;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/3/5.
 */
public class OKHttpErrorMsg extends NetWorkError {

    private Call call;
    private Exception e;

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public void setE(Exception e) {
        this.e = e;
    }

    public Exception getE() {
        return e;
    }
}
