package com.yt.bleandnfc.listener;

public interface SocketListener {

    /**
     * 接收到的数据
     */
    void receiveSocketData(String socketData);

    /**
     * 发送数据
     */
    void sendSocketData(String packs);

    /**
     * 发生错误
     */
    void error(Throwable e);
}
