package com.swst.sipServer;

import com.swst.domain.SDP;
import com.swst.sipServer.codes.SipMessageEvent;
import com.swst.utils.Generate;
import com.swst.utils.ParseUtil;
import com.swst.videoServer.PortSingleton;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.RequestLine;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import javax.sip.address.URI;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: fregun
 * @Date: 19-12-28 15:57
 * @Description:
 */
@Component
public class StreamSipHandler extends SimpleChannelInboundHandler<SipMessageEvent> {
    Map<String,SIPResponse> sipResponseMap = new HashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SipMessageEvent sipMessageEvent) throws Exception {
       if(sipMessageEvent.getMessage() instanceof SIPRequest){
           SIPRequest message = (SIPRequest) sipMessageEvent.getMessage();
           if(SIPRequest.REGISTER.equals(message.getCSeq().getMethod())){
           }
           else if(SIPRequest.INVITE.equals(message.getCSeq().getMethod())){//处理Invite信息
               if(message.getMessageContent() != null && message.getMessageContent().length()>1){
                   this.handleContainSdp(sipMessageEvent);
               }else{
                   handNoSdp(sipMessageEvent);
               }
           }else if(SIPRequest.ACK.equals(sipMessageEvent.getMessage().getCSeq().getMethod())){//处理ack信息

           }else if(SIPRequest.BYE.equals(message.getCSeq().getMethod())){
               SIPResponse sipResponse = sipResponseMap.get(message.getCallId().getCallId());
               if(sipResponse != null && sipResponse.getFromTag().equals(message.getFromTag()) && sipResponse.getToTag().equals(message.getToTag())){
                   sipResponseMap.remove(message.getCallId().getCallId());
                   SIPResponse response = message.createResponse(200);
                   sipMessageEvent.getConnection().send(response);
               }else{//返回会话不存在

               }
           }
           //判断Invite信息的有效性(判断)，暂时忽略
           if(message.getTo().getAddress()==null){

           }
       }
       if(sipMessageEvent.getMessage() instanceof SIPResponse){
           SIPResponse message = (SIPResponse) sipMessageEvent.getMessage();
           if(message.getStatusCode() == 200){

           }
           if(message.getStatusCode() == 401){
               SipServer.b = false;
               this.handleRegister(sipMessageEvent);
           }
       }

    }

    /**
     * 处理注册问题
     * @param sipMessageEvent
     */
    public void handleRegister(SipMessageEvent sipMessageEvent) throws ParseException {
        String sipCode = "isscid";
        String sipIp = "192.168.6.201";
        int sipPort = 5070;
        SIPResponse message = (SIPResponse) sipMessageEvent.getMessage();
        SIPRequest sipRequest = new SIPRequest();
        RequestLine requestLine = new RequestLine();
        SipUri sipUri = new SipUri();
        sipUri.setUser(sipCode);
        sipUri.setHost(sipIp);
        sipUri.setPort(sipPort);
        requestLine.setUri(sipUri);
        requestLine.setMethod(SIPRequest.REGISTER);
        sipRequest.setRequestLine(requestLine);
        sipRequest.setVia(message.getViaHeaders());
        sipRequest.setFrom(message.getFrom());
        sipRequest.setTo(message.getTo());
        sipRequest.setCallId(message.getCallId());
        sipRequest.addHeader("CSeq: 2 Register");
        sipRequest.addHeader("Contact: <"+message.getTo().getAddress()+">");
        //生成验证信息
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String authenHeader = message.getHeader("WWW-Authenticate").toString();
        //将authenHeader进行分解
        String[] split = authenHeader.split(",");
        String reaml = null;
        String nonce = null;
        if(split.length<2){
            SipServer.b = true;
            return;
        }
        for(String str : split){
            if(str.contains("realm")){
                reaml = str.split("\"")[1];
            }
            if(str.contains("nonce")){
                nonce = str.split("\"")[1];
            }
        }
        if(reaml == null || nonce == null){//空字符处理
            SipServer.b = true;
            return;
        }
        //验证算法
        //加密规则 1） HASH1=MD5(username:realm:password)
        //
        //        2） HASH2=MD5(method:uri)
        //
        //        3）Response=MD5(HA1:nonce:HA2)
        String streamCode = "60215231000024101";
        String streamIp = "192.168.6.153";
        int streamPort = 5060;
        //此处写死，实际从数据库查得passward查出，
        digest.update((streamCode+":"+reaml+":"+123456).getBytes());
        String s = new BigInteger(1, digest.digest()).toString(16);
        digest.update(("Register:"+message.getTo().getAddress()).getBytes());
        String s1 = new BigInteger(1, digest.digest()).toString(16);
        digest.update((s+":"+nonce+":"+s1).getBytes());
        String response = new BigInteger(1, digest.digest()).toString(16);
        //设置加密信息
        Authorization authorization = new Authorization();
        authorization.setUsername(streamCode);
        authorization.setRealm(reaml);
        authorization.setNonce(nonce);
        authorization.setAlgorithm("MD5");
        authorization.setParameter("uri","sip:"+streamCode+"@"+streamIp+":"+streamPort);
        authorization.setResponse(response);
        sipRequest.setHeader(authorization);
        sipMessageEvent.getConnection().send(sipRequest);
    }
    //处理包含sdp　ack 消息

    /**
     * 确认流发送者是否正确
     * @param sipMessageEvent
     */
    public void handleAckContainSdp(SipMessageEvent sipMessageEvent){

    }
    //处理不包含sdp ack消息

    /**
     * 将流发送到指定端口
     * @param sipMessageEvent
     */
    public void handleAckNoSdp(SipMessageEvent sipMessageEvent){

    }
    //处理不包含sdp的Invite信令请求消息
    public void handNoSdp(SipMessageEvent sipMessageEvent){
        String unUseMap = PortSingleton.getInstance().getUnUseMap();
        String[] split = unUseMap.split(",");
        //获取流接收端口和ip地址,这里暂时写死
        SIPRequest message = (SIPRequest) sipMessageEvent.getMessage();
        String sdp = new SDP().getV(ParseUtil.parseMessage(message.getTo().getAddress().toString()),
                split[1],Integer.parseInt(split[0]),0,0);//构建ok消息
        SIPResponse response = message.createResponse(200);
        response.setToTag(Generate.generateTag());
        response.setMessageContent(sdp.getBytes());
        sipResponseMap.put(message.getCallId().getCallId(),response);
        sipMessageEvent.getConnection().send(response);
    }
    //处理包含sdp的Invite信令请求消息
    public void handleContainSdp(SipMessageEvent sipMessageEvent){
        //流发送端口和发送地址,这里调用方法获取端口
        int port = 5061;
        String sdp = SDP.sdptop+port+SDP.sdpBottom;//构建ok消息
        SIPRequest message = (SIPRequest) sipMessageEvent.getMessage();
        SIPResponse response = message.createResponse(200);
        response.setToTag(Generate.generateTag());
        response.setMessageContent(sdp.getBytes());
        sipMessageEvent.getConnection().send(response);
        //解析流发送地址，暂时未做tag和callId分析
    }
}
