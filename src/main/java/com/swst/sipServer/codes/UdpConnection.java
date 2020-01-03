package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * @author cormye tcq9121@163.com
 * @Description todo
 * @sine 2019/11/13
 */
public class UdpConnection extends AbstractConnection {


    protected UdpConnection(Channel channel, InetSocketAddress remote) {
        super(channel, remote);
    }

    @Override
    public boolean isUDP() {
        return true;
    }

    @Override
    public void send(SIPMessage msg) {
        final DatagramPacket datagramPacket=new DatagramPacket(Unpooled.wrappedBuffer(msg.encode().getBytes()),getRemoteAddress());
        channel().writeAndFlush(datagramPacket);
    }
}
