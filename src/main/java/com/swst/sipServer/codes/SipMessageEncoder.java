package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author cormye tcq9121@163.com
 * @Description todo
 * @sine 2019/11/13
 */
public class SipMessageEncoder extends MessageToByteEncoder<SIPMessage> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SIPMessage message, ByteBuf byteBuf) throws Exception {

    }
}
