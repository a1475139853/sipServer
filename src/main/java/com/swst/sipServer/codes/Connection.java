package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;

import java.net.InetSocketAddress;

/**
 * @author cormye tcq9121@163.com
 * @Description Sip Connection链接
 * @sine 2019/11/13
 */
public interface Connection {


    /**
     * 获取正在监听的本地地址
     *
     * @return
     */
    String getLocalIpAddress();



    /**
     * 本地监听端口
     *
     * @return
     */
    int getLocalPort();


    /**
     * 获取Remote连接对象
     * @return
     */
    InetSocketAddress getRemoteAddress();
    /**
     * 获取此连接到的远程地址
     *
     * @return
     */
    String getRemoteIpAddress();
    /**
     * 获取连接到的远程端口
     * @return
     */
    int getRemotePort();

    boolean isUDP();

    boolean isTCP();

    void send(SIPMessage msg);

    public boolean connect();

}
