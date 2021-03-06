package com.swst.sipServer.udp;

import com.swst.config.SpringContextHolder;
import com.swst.config.StreamConfig;
import com.swst.sipServer.codes.SipMessageDatagramDecoder;
import com.swst.sipServer.codes.SipMessageEncoder;
import com.swst.sipServer.tcp.TcpSipServer;
import com.swst.videoOutServer.VideoOutServer;
import com.swst.videoRecServer.VideoServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * @Auther: fregun
 * @Date: 19-11-4 09:32
 * @Description:
 */
@Component
public class SipServer implements CommandLineRunner {

    private StreamConfig streamConfig = (StreamConfig) SpringContextHolder.getBean("streamConfig");
    public static boolean b = true;
    
    public void run(String... args){
        System.out.println("启动了");
//        new Thread(new Timer()).start();
        new Thread(new Runnable() {
            public void run() {
                new SipServer().start();
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                try {
                    new VideoServer(streamConfig.getRecPortStart(),streamConfig.getRecPortEnd()).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                try {
                    new VideoOutServer(streamConfig.getOutPortStart(),streamConfig.getOutPortEnd()).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new TcpSipServer().start();
            }
        }).start();
/*        for(int i=25061;i<=25067;i++){
            final int port = i;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        new VideoServer().start(port);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }*/


    }
    private void start(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(boss)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST,true)
                    .handler(new ChannelInitializer<NioDatagramChannel>(){
                        //NioDatagramChannel标志着是UDP格式的
                        @Override
                        protected void initChannel(NioDatagramChannel ch)
                                throws Exception {
                            //创建一个执行Handler的容器
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new SipMessageDatagramDecoder())
                                    .addLast("encoder", new SipMessageEncoder())
                                    .addLast(new StreamSipHandler());
                        }

                    });
            ChannelFuture future = bootstrap.bind(streamConfig.getIp(),streamConfig.getPort()).sync();
            System.out.println("5062 netty 启动完成");
            //启动后向sip服务器进行注册
/*
            while(b){
                this.register(future.channel());
                Thread.sleep(500);
            }
*/

            future.channel().closeFuture().await();
        }catch (Exception e ){
        }
        finally {
            //释放线程池资源
            work.shutdownGracefully();
        }
    }
/*
    public  SIPRequest register(Channel channel) throws ParseException {
        //从数据库取得sip服务器的国际编码，域名或者ip端口,这里暂时写死
        String sipCode = "isscid";
        String sipIp = "192.168.6.201";
        int sipPort = 5070;
        //流媒体服务器国际编码也是从数据库取得，这里暂时写死
        String streamCode = "60215231000024101";
        String streamIp = "192.168.6.153";
        int streamPort = 5062;
        SIPRequest sipRequest = new SIPRequest();
        RequestLine requestLine = new RequestLine();
        SipUri sipUri = new SipUri();
        sipUri.setUser(sipCode);
        sipUri.setHost(sipIp);
        sipUri.setPort(sipPort);
        requestLine.setUri(sipUri);
        requestLine.setMethod(SIPRequest.REGISTER);
        sipRequest.setRequestLine(requestLine);
        sipRequest.addHeader("Via: SIP/2.0/UDP "+streamIp+":"+streamPort);
        sipRequest.addHeader("From: <sip:" + streamCode +"@"+streamIp +":"+ streamPort +">" + Generate.generateTag());
        sipRequest.addHeader("To: sip:" + streamCode + "@" + streamIp +":"+ streamPort);
        sipRequest.addHeader("Call-ID: " + UUID.randomUUID().toString().replace("-",""));
        sipRequest.addHeader("Cseq: "+ 1 + "Register");
        sipRequest.addHeader("Contact: <sip:"+streamCode +"@"+streamIp +":"+ streamPort+">");
        sipRequest.addHeader("Max-Forwards: 70");
        sipRequest.addHeader("Expires: 3600");
        sipRequest.addHeader("Content-Length: " + 0);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(sipIp, sipPort);
        DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(sipRequest.encode().getBytes()), inetSocketAddress);
        System.out.println(sipRequest);
        channel.writeAndFlush(datagramPacket1);
        return sipRequest;
    }*/
}

