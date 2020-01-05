package com.swst.videoServer;

import com.swst.rtphandle.Parse;
import com.swst.rtphandle.RtpH264Parse;
import com.swst.websocket.ChannelManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static java.util.Arrays.copyOfRange;


/**
 * @Auther: fregun
 * @Date: 19-11-6 17:57
 * @Description:
 */
@ChannelHandler.Sharable
public class VideoHandle extends SimpleChannelInboundHandler<DatagramPacket> {
//    File file = new File("/root/Desktop/test.mp4");
    private final String LOCK = "LOCK";
//    FileOutputStream fileOutputStream = new FileOutputStream(file);
    boolean a = false;

    private BufferedImage bufferedImage;

    private RtpH264Parse rtpH264Parse = new RtpH264Parse();

    public VideoHandle(){
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket o) throws Exception {
        System.out.println("收到消息");
        int i= 0;
        System.out.println("--------------------");
        ByteBuf content = o.content();
        InetSocketAddress socketAddress = (InetSocketAddress) o.sender();
        String ip = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
        System.out.println("收到消息----------------"+ip + "++++++++" + port);
        System.out.println(rtpH264Parse);
        byte[]bytes1 = new byte[content.readableBytes()];
        content.readBytes(bytes1);
        byte[] bytes = copyOfRange(bytes1,14,bytes1.length);
        if(a){
            System.out.println();
            a = false;
        }
/*        for (int j=0;j<bytes1.length;j++) {
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

//        if(WriteMessageToSIP.ctx == null){
//            WriteMessageToSIP.ctx = channelHandlerContext;
//        }
//        if(o.content().toString().contains("200 OK")){
//            System.out.println(o.content().toString());
//        }
        rtpH264Parse.handleNalHeader(bytes1,ip);
//        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.201",1935);
//            DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(sipResponse.encode().getBytes()), inetSocketAddress);
//        DatagramPacket datagramPacket1 = new DatagramPacket(o.content(), inetSocketAddress);

//        channelHandlerContext.writeAndFlush(datagramPacket1);
//        ByteArrayOutputStream bo = new ByteArrayOutputStream();
//        ObjectOutputStream oo = new ObjectOutputStream(bo);
//        oo.writeObject(o);
//        byte[] bytes = bo.toByteArray();

//        fileOutputStream.write(bytes);
//        fileOutputStream.flush();
//        ByteBuffer bb = ByteBuffer.wrap(o.content().toString().getBytes());
//        H264Decoder decoder = new H264Decoder();
//        Picture picture = Picture.create(1920, 1088, ColorSpace.YUV420);
//        Picture frame = decoder.decodeFrame(bb, picture.getData());
//        byte[][] data = frame.getData();
//        File file = new File("/root/Desktop/sipServer/picture.jpg");
//        if(!file.exists()){
//            if(file.mkdir()){
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                for(int i=0;i<data.length;i++){
//                    fileOutputStream.write(data[i]);
//                }
//            }
//        }
    }
}

