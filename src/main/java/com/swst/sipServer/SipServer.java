package com.swst.sipServer;

import com.swst.sipServer.SipHandler;
import com.swst.videoServer.VideoServer;
import com.swst.websocket.WebsocketServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Auther: fregun
 * @Date: 19-11-4 09:32
 * @Description:
 */
@Component
public class SipServer implements CommandLineRunner {




    public void run(String... args) throws Exception {
        System.out.println("启动了");
        new Thread(new Timer()).start();
        new Thread(new Runnable() {
            public void run() {
                SipServer.this.start();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                try {
                    new VideoServer().start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                try {
                    new WebsocketServer().start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void start(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(boss)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new SipHandler());
            ChannelFuture future = bootstrap.bind("192.168.6.153",5060).sync();
            System.out.println("5060 netty 启动完成");
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

