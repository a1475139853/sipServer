package com.swst.videoServer;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: fregun
 * @Date: 19-11-6 17:52
 * @Description:
 */
@Component
public class VideoServer {

    private int beginPort;
    private int endPort;


    public VideoServer() {

    }

    public VideoServer(int beginPort, int endPort) {
        this.beginPort = beginPort;
        this.endPort = endPort;
    }


    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

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
//            future.channel().closeFuture().await();
                        System.out.println(port + " netty 启动成功");
                        PortSingleton.getInstance().unUseMap.put(port, "192.168.6.153");
//            PortSingleton.getInstance().unUseMap.put(port, InetAddress.getLocalHost().getHostAddress());
                    } catch (Exception e) {
                        System.out.println(port + " netty 启动失败");
                    }

                }
            }).start();
        }
/*        for (Map.Entry<Integer, String> map : PortSingleton.getInstance().unUseMap.entrySet()) {
            System.out.println("端口" + map.getKey() + "已加入未使用队列;本机IP为:" + map.getValue());
        }*/

        /*try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Bootstrap bootstrap = new Bootstrap();

                        bootstrap.group(boss)
                                .channel(NioDatagramChannel.class)
                                .option(ChannelOption.SO_BROADCAST,true)
                                .handler(new VideoHandle());
                        ChannelFuture future = bootstrap.bind("192.168.6.153",(int)5061).sync();
                        future.channel().closeFuture().await();
                        System.out.println("5061 netty 启动完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Bootstrap bootstrap = new Bootstrap();

                        bootstrap.group(boss)
                                .channel(NioDatagramChannel.class)
                                .option(ChannelOption.SO_BROADCAST,true)
                                .handler(new VideoHandle());
                        ChannelFuture future2 = bootstrap.bind("192.168.6.153",(int)5062).sync();
                        future2.channel().remoteAddress();

                        future2.channel().closeFuture().await();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e ){
        }
        finally {
            //释放线程池资源
//            boss.shutdownGracefully();
//            work.shutdownGracefully();
        }*/
    }

   /* private int port = 0;

    public VideoServer(){

    }

    public VideoServer(int port){
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new VideoHandle());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            ChannelFuture f = bootstrap.bind(port).sync();
            if(f.isSuccess()){
                System.out.println(port + "端口 netty 启动成功");
            }else {
                System.out.println(port + "端口 netty 启动失败");
            }
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.out.println(port + "端口 netty 启动失败");
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }*/
}
