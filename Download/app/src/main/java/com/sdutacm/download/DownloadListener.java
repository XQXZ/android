package com.sdutacm.download;

/**
 * 功能：对下载过程中的各种状态进行监听
 */

public interface DownloadListener {
    void onProgress(int progress); //用于通知当前的下载进度
    void onSuccess();              //用于通知下载成功事件
    void onFailed();               //用于通知下载失败事件
    void onPaused();               //用于通知下载暂停事件
    void onCanceled();             //用于通知下载取消事件
}