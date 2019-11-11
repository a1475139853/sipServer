package com.swst.videoServer;

import com.swst.sipServer.Timer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import utils.WriteMessageToSIP;


/**
 * @Auther: fregun
 * @Date: 19-11-6 17:57
 * @Description:
 */
public class VideoHandle extends SimpleChannelInboundHandler<DatagramPacket> {
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket o) throws Exception {
        System.out.println("收到消息");
        System.out.println("--------------\n"+o.content().toString(CharsetUtil.UTF_8)+"\n-----------------");
        if(WriteMessageToSIP.ctx == null){
            WriteMessageToSIP.ctx = channelHandlerContext;
        }
        Timer.b = true;

    }
}
