package com.swst.videoRecServer;

import com.swst.domain.DataInfo;
import com.swst.domain.IpAndPort;
import com.swst.domain.UDPIpAndPort;
import com.swst.rtphandle.RtpH264Parse;
import com.swst.sipServer.udp.StreamSipHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.Map;

import static java.util.Arrays.copyOfRange;


/**
 * @Auther: fregun
 * @Date: 19-11-6 17:57
 * @Description:
 */
@ChannelHandler.Sharable
public class VideoHandle extends SimpleChannelInboundHandler<DatagramPacket> {
    private final String LOCK = "LOCK";
    boolean a = false;

    private RtpH264Parse rtpH264Parse = new RtpH264Parse();

    public VideoHandle(){
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket o) throws Exception {
//        System.out.println("收到消息");
        int i= 0;
//        System.out.println("--------------------");
        ByteBuf content = o.content();
        InetSocketAddress socketAddress = (InetSocketAddress) o.sender();
        String ip = socketAddress.getAddress().getHostAddress();
        int port = socketAddress.getPort();
//        System.out.println("收到消息----------------"+ip + "++++++++" + port);
//        System.out.println(rtpH264Parse);
        byte[]bytes1 = new byte[content.readableBytes()];
        content.readBytes(bytes1);
        byte[] bytes = copyOfRange(bytes1,14,bytes1.length);
        if(a){
            System.out.println();
            a = false;
        }
        //        System.out.println("--------------------");

//        rtpH264Parse.handleNalHeader(bytes1,ip);
        String ipPort=ip+port;
       // UDPIpAndPort ipAndPort = StreamSipHandler.ipAndPortMapUdp.get(ip);
        UDPIpAndPort ipAndPort = StreamSipHandler.ipAndPortMapUdp.get(ip+":"+port);
        if (ipAndPort != null && ipAndPort.isPush()) {
            //设置阀值
             UDPIpAndPort udpIpAndPort = PortSingleton.getInstance().getUseSendData().get(ip + port);
                      if(udpIpAndPort!=null){
                          udpIpAndPort.setThreshold(0);
                          PortSingleton.getInstance().getUseSendData().put(ip + port,udpIpAndPort);
                      }
             InetSocketAddress inetSocketAddress = new InetSocketAddress(ipAndPort.getRecIp(),ipAndPort.getRecPort());
            DatagramPacket datagramPacket = new DatagramPacket(Unpooled.wrappedBuffer(bytes1), inetSocketAddress);
            ipAndPort.getChannel().writeAndFlush(datagramPacket);
           //发送数据  设置阀值为0
            ipAndPort.setThreshold(0);



        }

         //接收数据  将接收端阀值置   置0
         String  str=ip+port;

         PortSingleton.getInstance().resetRecThreshold(str);
//         Map<String, DataInfo> useCodeDataMap = PortSingleton.getInstance().getUseCodeDataMap();
//         Map<String, DataInfo> useIpPortDataMap = PortSingleton.getInstance().getUseIpPortDataMap();
//         DataInfo dataInfo = useIpPortDataMap.get(str);
//         if(dataInfo!=null){
//             String cameraCode = dataInfo.getCameraCode();
//             dataInfo.setThreshold(0);
//             useIpPortDataMap.put(str,dataInfo);
//             useCodeDataMap.put(cameraCode,dataInfo);
//         }

    }
}

