package com.swst.neety;

import com.swst.utils.IpAndPort;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

public class NettyCustomerClient {
    /**
     * netty连接
     * @param port 端口
     */
    public IpAndPort client(String ip, int port){

        Bootstrap bootstrap = new Bootstrap();

        NioEventLoopGroup group = new NioEventLoopGroup();

        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<NioSocketChannel>(){
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                nioSocketChannel.pipeline().addLast(new ObjectEncoder());
                nioSocketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
//                nioSocketChannel.pipeline().addLast(new SimpleClientHandler());
            }
        });
        try {
            ChannelFuture sync = bootstrap.connect(ip, port).sync();

            //获取本客户端ip、端口
            InetSocketAddress inetSocketAddress = (InetSocketAddress) sync.channel().remoteAddress();
            String myIp = inetSocketAddress.getAddress().getHostAddress();
            int myPort = inetSocketAddress.getPort();

            //传输ip和端口 sync对象
            IpAndPort ipAndPort = new IpAndPort();
            ipAndPort.setIp(myIp);
            ipAndPort.setPort(myPort);
            ipAndPort.setSync(sync);
            //发送
            sync.channel().writeAndFlush(ipAndPort);
            //myIp、sync对象存入map
            sync.channel().closeFuture().sync();
            return ipAndPort;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
