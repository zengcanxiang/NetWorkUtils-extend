package com.zengcanxiang.oknetworkutils;

import com.zengcanxiang.network.NetWork;
import com.zengcanxiang.network.NetWorkCallback.DownCallback;
import com.zengcanxiang.network.NetWorkCallback.NetWorkCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zengcanxiang on 2016/2/26.
 */
public class OkHttpNetWork<T> extends NetWork<T> {

    @Override
    public void post(HashMap<String, String> paramsMap, String url, final NetWorkCallback<T> callback,
                     Object tag, int... what) {
        PostFormBuilder post = OkHttpUtils.post();
        post.params(paramsMap);
        Callback<OkNetWorkResponse> okHttpCallback = convertNetWorkCallback(callback);
        post.url(url).tag(tag).build().execute(okHttpCallback);
    }

    @Override
    public void get(HashMap<String, String> paramsMap, String url, final NetWorkCallback<T> callback,
                    Object tag, int... what) {
        GetBuilder get = OkHttpUtils.get();
        get.params(paramsMap);
        Callback<OkNetWorkResponse> okHttpCallback = convertNetWorkCallback(callback);
        get.url(url).tag(tag).build().execute(okHttpCallback);
    }


    @Override
    public void uploadFile(String uploadUrl, HashMap<String, String> paramsMap,
                           String fileKey, File file,
                           final NetWorkCallback<T> callback, Object uploadFileTag, int... what) {

        Callback<OkNetWorkResponse> okHttpCallback = convertNetWorkCallback(callback);

        OkHttpUtils.postFile()
                .url(uploadUrl)
                .file(file)
                .tag(uploadFileTag)
                .build()
                .execute(okHttpCallback);
    }

    @Override
    public void uploadFiles(String uploadUrl, HashMap<String, String> paramsMap,
                            ArrayList<String> fileKeys, ArrayList<String> fileNames, ArrayList<File> files,
                            final NetWorkCallback<T> callback, Object uploadFileTag, int... what) {
        PostFormBuilder post = OkHttpUtils.post();
        post.params(paramsMap);
        post.url(uploadUrl);
        post.tag(uploadFileTag);
        int size = fileNames.size();
        if (size != files.size() || size != fileKeys.size() || files.size() != fileKeys.size()) {
            throw new IllegalAccessError("files size not equal fileKeys size or fileNames size;\n " +
                    "files.size=" + files.size() + ",fileKeys.size=" + fileKeys.size() + ",fileNames.size=" + fileNames.size());
        }
        for (int i = 0; i < size; i++) {
            post.addFile(fileKeys.get(i), fileNames.get(i), files.get(i));
        }

        Callback<OkNetWorkResponse> okHttpCallback = convertNetWorkCallback(callback);

        post.build().execute(okHttpCallback);
    }

    @Override
    public void downLoadFile(String downUrl, String savePath, String saveFileName,
                             long connTimeOut, long readTimeOut, long writeTimeOut,
                             NetWorkCallback<T> callBack, Object downFileTag, int... what) {

        if (callBack instanceof DownCallback) {
            throw new IllegalArgumentException("this is downLoad file,callback is DownCallback");
        }

        final DownCallback downCallback = (DownCallback) callBack;

        FileCallback fileCallback = new FileCallback(savePath, saveFileName) {

            @Override
            public void inProgress(float v, long total) {

                downCallback.onProgress(v, total, -1);
            }

            @Override
            public void onError(Call call, Exception e) {

                downCallback.onError(getError(call, e));
            }

            @Override
            public void onResponse(File file) {
                downCallback.onDownSucceed(file.getPath());
            }
        };

        OkHttpUtils.get().url(downUrl).tag(downFileTag).build()
                .connTimeOut(connTimeOut)
                .readTimeOut(readTimeOut)
                .writeTimeOut(writeTimeOut)
                .execute(fileCallback);
    }

    @Override
    public void cancel(Object tag) {
        OkHttpUtils.getInstance().cancelTag(tag);
    }

    private Callback<OkNetWorkResponse> convertNetWorkCallback(final NetWorkCallback<T> callback) {
        Callback<OkNetWorkResponse> okCallback = new Callback<OkNetWorkResponse>() {
            @Override
            public OkNetWorkResponse parseNetworkResponse(Response response) throws Exception {
                OkNetWorkResponse<T> okNetWorkResponse = new OkNetWorkResponse<T>(response);
                return okNetWorkResponse;
            }

            @Override
            public void onError(Call call, Exception e) {
                callback.onError(getError(call, e));
            }

            @Override
            public void onResponse(OkNetWorkResponse okNetWorkResponse) {
                callback.onSucceed(okNetWorkResponse, -1);
            }
        };
        return okCallback;
    }

    private OKHttpErrorMsg getError(Call call, Exception e) {
        OKHttpErrorMsg errorMsg = new OKHttpErrorMsg();
        errorMsg.setE(e);
        errorMsg.setCall(call);
        return errorMsg;
    }
}
