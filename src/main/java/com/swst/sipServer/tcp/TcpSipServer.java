package com.swst.sipServer.tcp;

import com.swst.config.SpringContextHolder;
import com.swst.config.StreamRecConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.springframework.stereotype.Component;

/**
 * @Auther: fregun
 * @Date: 20-1-7 10:40
 * @Description:
 */
@Component
public class TcpSipServer {
    private StreamRecConfig streamRecConfig = (StreamRecConfig) SpringContextHolder.getBean("streamRecConfig");

    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    System.out.println("有客户端连接上来:" + ch.remoteAddress().toString());
                    ch.pipeline().addLast(new TcpSipHandle());
                }
            });
            ChannelFuture f = b.bind(streamRecConfig.getTcpPort()).sync();
            System.out.println("file server 等待连接：");
            f.channel().closeFuture().sync();
            System.out.println("file end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        }
    }

