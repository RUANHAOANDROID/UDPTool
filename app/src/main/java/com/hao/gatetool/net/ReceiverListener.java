package com.hao.gatetool.net;

/**
 * @date: 2022/12/1
 * @author: 锅得铁
 * #
 */
public interface ReceiverListener {
    /**
     * 收消息回调
     *
     * @param message
     * @param ipAddress
     */
    void messageArrived(String message, String ipAddress, int port);
}
