package com.swst.sipServer.codes;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author cormye tcq9121@163.com
 * @Description todo
 * @sine 2019/11/13
 */
public abstract class AbstractConnection implements Connection{

    private final Channel channel;
    private final InetSocketAddress remote;

    AbstractConnection(final Channel channel, final InetSocketAddress remote) {
        this.channel = channel;
        this.remote = remote;
    }

    protected Channel channel(){
        return this.channel;
    }

    @Override
    public String getLocalIpAddress() {
        final SocketAddress local = this.channel.localAddress();
        return ((InetSocketAddress) local).getAddress().getHostAddress();
    }

    @Override
    public int getLocalPort() {
        final SocketAddress local = this.channel.localAddress();
        return ((InetSocketAddress) local).getPort();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.remote;
    }

    @Override
    public String getRemoteIpAddress() {
        return this.remote.getAddress().getHostAddress();
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public boolean isUDP() {
        return false;
    }

    @Override
    public boolean isTCP() {
        return false;
    }

    @Override
    public boolean connect() {
        return false;
    }
}
