package com.swst.videoServer;

import com.swst.rtphandle.RtpH264Parse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.net.InetSocketAddress;

import static java.util.Arrays.copyOfRange;


/**
 * @Auther: fregun
 * @Date: 19-11-6 17:57
 * @Description:
 */
public class VideoPullHandle extends SimpleChannelInboundHandler<DatagramPacket> {
//    File file = new File("/root/Desktop/test.mp4");
    private final String LOCK = "LOCK";
//    FileOutputStream fileOutputStream = new FileOutputStream(file);
    boolean a = false;

    private BufferedImage bufferedImage;

    private RtpH264Parse rtpH264Parse = new RtpH264Parse();

    public VideoPullHandle() throws FileNotFoundException {
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket o) throws Exception {
        int i= 0;
        System.out.println("--------------------");
        ByteBuf content = o.content();
        InetSocketAddress inetSocketAddress = (InetSocketAddress)o.sender();
        String ip = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        System.out.println("收到消息----------------"+ip + "++++++++" + port);
        System.out.println(rtpH264Parse);
        byte[]bytes1 = new byte[content.readableBytes()];
        content.readBytes(bytes1);
        byte[] bytes = copyOfRange(bytes1,14,bytes1.length);
        if(a){
            System.out.println();
            a = false;
        }
       /* for (int j=0;j<bytes1.length;j++) {
            String s = Integer.toHexString(0xFF & bytes1[j]);
//            String s1 = Integer.toHexString(0xFF & bytes[j]);
            if(j==1 && s.equals("e3")){
                System.out.println();
                a = true;

            }
            if(s.length()<2){
                System.out.print("0"+s+"  ");
            }else{
                System.out.print(s+"  ");
            }
            if(i == 16){
                System.out.println();
                i=0;
            }
            ++i;

        }*/
        System.out.println("--------------------");
        channelHandlerContext.channel().remoteAddress();
        channelHandlerContext.channel().localAddress();
        rtpH264Parse.handleNalHeader(bytes1,ip);

    }
}

