package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * @author cormye tcq9121@163.com
 * @Description todo
 * @sine 2019/11/13
 */
public class TcpConnection extends AbstractConnection {
    protected TcpConnection(Channel channel, InetSocketAddress remote) {
        super(channel, remote);
    }

    @Override
    public void send(SIPMessage msg) {

    }
}
