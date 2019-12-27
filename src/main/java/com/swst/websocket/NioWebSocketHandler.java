package com.swst.websocket;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;


@Slf4j

public class NioWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private WebSocketServerHandshaker handshaker;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg){

        if (msg instanceof FullHttpRequest){
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        }else if (msg instanceof WebSocketFrame){
            if (msg instanceof TextWebSocketFrame){
                try {
                    TextWebSocketFrame text = (TextWebSocketFrame)msg;
                    System.out.println("收到消息："+text.text());
                    //处理websocket客户端的消息
                    TextWebSocketFrame t = new TextWebSocketFrame(text.text());
                    ctx.writeAndFlush(t);
                    System.out.println("send out");
                }catch (Exception e ){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx){
        //添加连接
        System.out.println("客户端加入连接："+ctx.channel());
        ChannelManage.add(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //断开连接
        System.out.println("客户端断开连接："+ctx.channel());
        ChannelManage.remove(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame){
//        // 判断是否关闭链路的指令
//        if (frame instanceof CloseWebSocketFrame) {
//            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
//            return;
//        }
//        // 判断是否ping消息
//        if (frame instanceof PingWebSocketFrame) {
//            ctx.channel().write(
//                    new PongWebSocketFrame(frame.content().retain()));
//            return;
//        }
//        // 支持文本消息，不支持二进制消息
//        if (!(frame instanceof TextWebSocketFrame)) {
//            log.debug("仅支持文本消息，不支持二进制消息");
//            throw new UnsupportedOperationException(String.format(
//                    "%s frame types not supported", frame.getClass().getName()));
//        }
    }
    /**
     * 唯一的一次http请求，用于创建websocket
     * */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
//        if (!req.decoderResult().isSuccess()
//                || (!"websocket".equals(req.headers().get("Upgrade")))) {
//            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
//            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
//                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
//            return;
//        }
       try {
           WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                   "ws://localhost:8082/wsh264", null, false);
           handshaker = wsFactory.newHandshaker(req);
           if (handshaker == null) {
               WebSocketServerHandshakerFactory
                       .sendUnsupportedVersionResponse(ctx.channel());
           } else {
               handshaker.handshake(ctx.channel(), req);
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

}