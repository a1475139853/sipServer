package com.swst.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * @Auther: fregun
 * @Date: 19-11-5 15:14
 * @Description:
 */
public class  WriteMessageToSIP {
    public static  ChannelHandlerContext ctx;
    public static String to;
    public static String CSeq;
    public static String Call_ID;
    public static String Via;
    public static String From;
    public static String user;

}
