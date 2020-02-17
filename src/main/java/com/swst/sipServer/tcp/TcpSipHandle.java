package com.swst.sipServer.tcp;


import com.swst.domain.DataInfo;
import com.swst.rtphandle.RtpH264Parse;
import com.swst.videoRecServer.PortSingleton;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @Auther: fregun
 * @Date: 20-1-7 10:43
 * @Description:
 */
@Component
public class TcpSipHandle extends ChannelInboundHandlerAdapter {

    private RtpH264Parse rtpH264Parse = new RtpH264Parse();
    File file = new File("/root/Desktop/test.ts");
    FileOutputStream out = new FileOutputStream(file);

    public TcpSipHandle() throws FileNotFoundException {
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf msg1 = (ByteBuf) msg;
        //创建目标大小的数组
        byte[] barray = new byte[msg1.readableBytes()];
        msg1.getBytes(0,barray);
        int i = 0;
        for(byte b:barray){
            if(i==11){
                System.out.println();
                i=0;
            }
            i++;
            System.out.print(b&0xff);
            System.out.print("　");
        }
        System.out.println("===========================");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String address = inetSocketAddress.getAddress().getHostAddress();
        int port = inetSocketAddress.getPort();
        out.write(barray);
        out.flush();
//        rtpH264Parse.handleNalHeader(barray,address);

        //接收数据  将阀值置   置空0
        String  str=address+port;
        Map<String, DataInfo> useCodeDataMap = PortSingleton.getInstance().getUseCodeDataMap();
        Map<String, DataInfo> useIpPortDataMap = PortSingleton.getInstance().getUseIpPortDataMap();
        DataInfo dataInfo = useIpPortDataMap.get(str);
        if(dataInfo!=null){
            String cameraCode = dataInfo.getCameraCode();
            dataInfo.setThreshold(0);
            useIpPortDataMap.put(str,dataInfo);
            useCodeDataMap.put(cameraCode,dataInfo);
        }
    }


}
