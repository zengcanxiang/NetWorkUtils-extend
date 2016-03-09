package com.zengcanxiang.oknetworkutils;

import com.zengcanxiang.network.NetWorkCallback.NetWorkCallback;
import com.zengcanxiang.network.NetWorkResponse;

/**
 * Created by Administrator on 2016/3/5.
 */
public abstract class OkCallBack<T> extends NetWorkCallback<T> {

    public abstract void onStart();

    public abstract void onFinish();

    public abstract void onSucceed(NetWorkResponse<T> response);

    public abstract void onProgress(float progress, long fileCount);

    @Override
    public void onStart(int NoHttpWhat) {
        onStart();
    }

    @Override
    public void onFinish(int NoHttpWhat) {
        onFinish();
    }

    @Override
    public void onSucceed(NetWorkResponse<T> response, int NoHttpWhat) {
        onSucceed(response);
    }

    @Override
    public void onProgress(float progress, long fileCount, int NoHttpWhat) {
        onProgress(progress, fileCount);
    }


}
