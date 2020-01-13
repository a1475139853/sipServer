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
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
//@Component
public class NettyCustomerClient {

    private String ip;
    private int port;
    private IpAndPort ipAndPort;
    public IpAndPort getIpAndPort(){
        return ipAndPort;
    }
    public void setIpAndPort(IpAndPort ipAndPort){
        this.ipAndPort = ipAndPort;
    }
    public NettyCustomerClient(String ip,int port){
        this.ip = ip;
        this.port = port;
    }
    public NettyCustomerClient(){};


    public void run() {
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
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) sync.channel().localAddress();
                    String myIp = inetSocketAddress.getAddress().getHostAddress();
                    int myPort = inetSocketAddress.getPort();

                    //传输ip和端口 sync对象
                    IpAndPort ipAndPort = new IpAndPort();
                    ipAndPort.setIp(myIp);
                    ipAndPort.setPort(myPort);
                    ipAndPort.setSync(sync);
                    setIpAndPort(ipAndPort);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

    }
}
