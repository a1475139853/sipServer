package com.swst.videoOutServer;

import com.swst.videoRecServer.PortSingleton;
import com.swst.videoRecServer.VideoHandle;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * @Auther: fregun
 * @Date: 20-2-15 16:00
 * @Description:
 */
public class VideoOutServer {

    private int beginPort;
    private int endPort;


    public VideoOutServer() {

    }

    public VideoOutServer(int beginPort, int endPort) {
        this.beginPort = beginPort;
        this.endPort = endPort;
    }


    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        PortSingleton instance = PortSingleton.getInstance();
        for (int i = beginPort; i <= endPort; i++) {
            final int port = i;

            new Thread(new Runnable() {
                public void run() {
                    try {
                        Bootstrap bootstrap = new Bootstrap();

                        bootstrap.group(boss)
                                .channel(NioDatagramChannel.class)
                                .option(ChannelOption.SO_BROADCAST, true)
                                .handler(new VideoHandle());
                        ChannelFuture future = bootstrap.bind(port).sync();
                        instance.addOutPort(port,future.channel());//存储接收流未使用端口
                        System.out.println(port + " netty 启动成功");
                    } catch (Exception e) {
                        System.out.println(port + " netty 启动失败");
                    }

                }
            }).start();
        }
    }
}
