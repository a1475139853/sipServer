package com.swst.sipServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: fregun
 * @Date: 19-11-8 10:26
 * @Description:
 */
public class InviteHandle extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        String s = datagramPacket.content().toString(CharsetUtil.UTF_8);
        Map<String,String> headers = new HashMap<String, String>();
        System.out.println("Invite检测： "+s);
        String[] split = s.split("\r\n");
        if(split[0].contains("200 OK")){
            System.out.println("成功返回");
            Timer.b = false;
        }
    }
}
