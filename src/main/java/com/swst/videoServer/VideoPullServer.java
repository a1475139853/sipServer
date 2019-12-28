package com.swst.videoServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.stereotype.Component;

/**
 * @Auther: fregun
 * @Date: 19-11-6 17:52
 * @Description:
 */
@Component
public class VideoPullServer {
    public void start(int port) throws Exception {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(boss)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new VideoPullHandle());
            ChannelFuture future = bootstrap.bind("192.168.6.153",port).sync();
            System.out.println("5061 netty 启动完成");
            future.channel().closeFuture().await();
        }catch (Exception e ){
        }
        finally {
            //释放线程池资源
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
