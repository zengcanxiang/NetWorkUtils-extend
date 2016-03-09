package com.zengcanxiang.nonetworkutils;

import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.RequestQueue;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadQueue;
import com.yolanda.nohttp.download.DownloadRequest;
import com.zengcanxiang.network.NetWork;
import com.zengcanxiang.network.NetWorkCallback.DownCallback;
import com.zengcanxiang.network.NetWorkCallback.NetWorkCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2016/3/2.
 */
public class NoHttpNetWork<T> extends NetWork<T> {

    private RequestQueue requestQueue;

    private DownloadQueue downloadQueue;

    /**
     * @param requestSize The core of concurrent requests
     */
    private NoHttpNetWork(int requestSize) {
        requestQueue = NoHttp.newRequestQueue(requestSize);
    }

    private void initDownloadQueue() {
        if (downloadQueue == null)
            downloadQueue = NoHttp.newDownloadQueue();
    }

    private static NoHttpNetWork mThis;

    public synchronized static NoHttpNetWork getInstance() {
        return getInstance(3);
    }

    public synchronized static NoHttpNetWork getInstance(int requestSize) {
        if (mThis == null) {
            synchronized (NoHttpNetWork.class) {
                if (mThis == null) {
                    mThis = new NoHttpNetWork(requestSize);
                }
            }
        }
        return mThis;
    }

    /**
     * @param what 用来标志请求的what, 类似handler的what一样，这里用来区分请求
     */
    @Override
    public void post(HashMap<String, String> paramsMap, String url,
                     NetWorkCallback<T> callback, Object tag, int... what) {
        netWork(paramsMap, url, RequestMethod.POST, callback, tag, what);
    }

    @Override
    public void get(HashMap<String, String> paramsMap, String url,
                    NetWorkCallback<T> callback, Object tag, int... what) {
        netWork(paramsMap, url, RequestMethod.GET, callback, tag, what);
    }

    @Override
    public void uploadFile(String uploadUrl, HashMap<String, String> paramsMap,
                           String fileKey, File file,
                           NetWorkCallback<T> callback, Object uploadFileTag, int... what) {
        ArrayList<String> fileKeys = new ArrayList<>();
        fileKeys.add(fileKey);
        ArrayList<String> fileNames = new ArrayList<>();
        fileNames.add(file.getName());
        ArrayList<File> files = new ArrayList<>();
        files.add(file);

        uploadFile(uploadUrl, paramsMap, fileKeys, fileNames, files, callback, uploadFileTag, what);
    }

    @Override
    public void uploadFiles(String uploadUrl, HashMap<String, String> paramsMap,
                            ArrayList<String> fileKeys, ArrayList<String> fileNames, ArrayList<File> files,
                            NetWorkCallback<T> callback, Object uploadFileTag, int... what) {
        uploadFile(uploadUrl, paramsMap, fileKeys, fileNames, files, callback, uploadFileTag, what);
    }


    public Request uploadFile(String uploadUrl, HashMap<String, String> paramsMap,
                              ArrayList<String> fileKeys, ArrayList<String> fileNames, ArrayList<File> files,
                              NetWorkCallback<T> callback, Object uploadFileTag, int... what) {

        ArrayList<FileBinary> fileBinaries = new ArrayList<>();
        for (int i = 0; i < fileKeys.size(); i++) {
            fileBinaries.add(new FileBinary(files.get(i)));
        }
        return uploadFile(uploadUrl, paramsMap,
                fileKeys, fileNames, files, fileBinaries,
                callback, uploadFileTag, what);
    }

    /**
     * 需要文件上传进度显示控制的
     *
     * @param uploadUrl     文件上传路径
     * @param paramsMap     如果需要带参 数
     * @param fileKeys      文件对应带key
     * @param fileNames     文件的名字
     * @param files         文件
     * @param fileBinaries  NoHttp文件上传类
     * @param callback      文件上传回调
     * @param uploadFileTag 文件上传请求tag
     * @param what          请求标识
     * @return
     */
    public Request uploadFile(String uploadUrl, HashMap<String, String> paramsMap,
                              ArrayList<String> fileKeys, ArrayList<String> fileNames, ArrayList<File> files,
                              ArrayList<FileBinary> fileBinaries,
                              NetWorkCallback<T> callback, Object uploadFileTag, int... what) {
        if (what.length == 0) {
            throw new IllegalArgumentException("what is null?");
        }

        Request<String> request = NoHttp.createStringRequest(uploadUrl, RequestMethod.POST);
        Set<String> key = paramsMap.keySet();
        for (Iterator<String> it = key.iterator(); it.hasNext(); ) {
            String s = it.next();
            request.add(s, paramsMap.get(s));
        }

        for (int i = 0; i < fileKeys.size(); i++) {
            request.add(fileKeys.get(i), fileBinaries.get(i));
        }

        request.setTag(uploadFileTag);
        request.setCancelSign(uploadFileTag);
        OnResponseListener listener = convertNetWorkCallback(callback);
        requestQueue.add(what[0], request, listener);
        return request;
    }

    @Override
    public void downLoadFile(String downUrl, String savePath, String saveFileName,
                             long connTimeOut, long readTimeOut, long writeTimeOut,
                             NetWorkCallback<T> callBack, Object downFileTag, int... what) {
        downLoadFile(downUrl,
                savePath, saveFileName, connTimeOut, readTimeOut,
                false, false,
                callBack, downFileTag, what);
    }

    /**
     * 支持断点续传
     */
    public DownloadRequest downLoadFile(String downUrl, String savePath, String saveFileName,
                                        long connTimeOut, long readTimeOut,
                                        boolean isRange, boolean isDeleteOld,
                                        NetWorkCallback<T> callBack, Object downFileTag, int... what) {
        initDownloadQueue();
        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(downUrl, savePath, saveFileName, isRange, isDeleteOld);
        downloadRequest.setCancelSign(downFileTag);
        downloadRequest.setTag(downFileTag);
        downloadRequest.setConnectTimeout(new Long(connTimeOut).intValue());
        downloadRequest.setReadTimeout(new Long(readTimeOut).intValue());
        if(what.length==0){
            throw new IllegalArgumentException("what is null?");
        }

        final DownCallback downCallback = (DownCallback) callBack;

        DownloadListener listener = new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {
                NoHttpErrorMsg errorMsg = new NoHttpErrorMsg();
                errorMsg.setException(exception);
                errorMsg.setWhat(what);
                downCallback.onError(errorMsg);
            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                downCallback.onStart(what);
            }

            @Override
            public void onProgress(int what, int progress, long fileCount) {
                downCallback.onProgress(progress, fileCount, what);
            }

            @Override
            public void onFinish(int what, String filePath) {
                downCallback.onDownSucceed(filePath);
                downCallback.onFinish(what);
            }

            @Override
            public void onCancel(int what) {
                downCallback.onCancel(what);
            }
        };

        downloadQueue.add(what[0], downloadRequest, listener);
        return downloadRequest;
    }

    @Override
    public void cancel(Object tag) {
        requestQueue.cancelBySign(tag);
        downloadQueue.cancelBySign(tag);
    }

    public void cancelAllNetWork() {
        requestQueue.cancelAll();
        downloadQueue.cancelAll();
    }


    public Request netWork(HashMap<String, String> paramsMap, String url,
                           RequestMethod method,
                           NetWorkCallback<T> callback, Object tag, int... what) {
        Request request= putParameter(paramsMap,url,method,tag);
        netWork(request, callback, what);
        return request;
    }

    public void netWork(Request request, NetWorkCallback<T> callback, int... what) {
        OnResponseListener onResponseListener = convertNetWorkCallback(callback);
        if (what.length == 0) {
            throw new IllegalArgumentException("what is null?");
        }
        requestQueue.add(what[0], request, onResponseListener);
    }
    private Request putParameter(HashMap<String, String> paramsMap, String url,
                                 RequestMethod method, Object tag){
        Request<String> request = NoHttp.createStringRequest(url, method);
        Set<String> key = paramsMap.keySet();
        for (Iterator<String> it = key.iterator(); it.hasNext(); ) {
            String s = it.next();
            request.add(s, paramsMap.get(s));
        }
        request.setCancelSign(tag);
        request.setTag(tag);
        return request;
    }

    private OnResponseListener convertNetWorkCallback(final NetWorkCallback<T> netWorkCallback) {
        OnResponseListener onResponseListener = new OnResponseListener<T>() {
            @Override
            public void onStart(int what) {
                netWorkCallback.onStart(what);
            }

            @Override
            public void onSucceed(int what, Response<T> response) {
                NoNetWorkResponse<T> noNetWorkResponse=new NoNetWorkResponse<T>(response);
                netWorkCallback.onSucceed(noNetWorkResponse, what);
            }

            @Override
            public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
                NoHttpErrorMsg errorMsg = new NoHttpErrorMsg();
                errorMsg.setTag(tag);
                errorMsg.setWhat(what);
                errorMsg.setUrl(url);
                errorMsg.setException(exception);
                errorMsg.setResponseCode(responseCode);
                errorMsg.setNetworkMillis(networkMillis);
                netWorkCallback.onError(errorMsg);
            }

            @Override
            public void onFinish(int what) {
                netWorkCallback.onFinish(what);
            }
        };
        return onResponseListener;
    }


}

