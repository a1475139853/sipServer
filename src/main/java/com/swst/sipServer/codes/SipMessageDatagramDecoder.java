package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.parser.StringMsgParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author cormye tcq9121@163.com
 * @Description todo
 * @sine 2019/11/13
 */
public class SipMessageDatagramDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final StringMsgParser stringMsgParser = new StringMsgParser();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket message, List<Object> out) throws Exception {
        final ByteBuf content = message.content();
        //忽略掉部分发过来的ping或者其他非SIP的UDP消息
        //SIP消息 不可能小于20个字节
        if (content.readableBytes() < 20) {
            return;
        }
        final  byte[] data=new byte[content.readableBytes()];
        content.getBytes(0,data);

        final Connection connection=new UdpConnection(channelHandlerContext.channel(),message.sender());
        final SIPMessage sipMessage = stringMsgParser.parseSIPMessage(data, true, false, null);
        final SipMessageEvent event=new DefaultSipMessageEvent(connection,sipMessage);
        out.add(event);

    }
}
