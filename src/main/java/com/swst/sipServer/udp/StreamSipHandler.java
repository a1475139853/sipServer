package com.swst.sipServer.udp;

import com.swst.config.SpringContextHolder;
import com.swst.config.StreamConfig;
import com.swst.config.StreamRecConfig;
import com.swst.domain.*;
import com.swst.service.video.impl.VideoServiceImpl;
import com.swst.sipServer.codes.SipMessageEvent;
import com.swst.utils.Generate;
import com.swst.utils.ParseUtil;
import com.swst.videoRecServer.PortSingleton;
import com.swst.vo.VideoVO;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: fregun
 * @Date: 19-12-28 15:57
 * @Description:
 */
@Component
public class StreamSipHandler extends SimpleChannelInboundHandler<SipMessageEvent> {
    private VideoServiceImpl videoService = (VideoServiceImpl)SpringContextHolder.getBean("videoServiceImpl");
    private StreamConfig streamConfig = (StreamConfig)SpringContextHolder.getBean("streamConfig");
    private StreamRecConfig streamＲecConfig = (StreamRecConfig)SpringContextHolder.getBean("streamRecConfig");
    Map<String,SIPResponse> sipResponseMap = new HashMap<>();
    Map<String,String>ipMap = new HashMap<>();//根据cameraCode存储对应ip信息
    public static Map<String,IpAndPort> ipAndPortMapTcp = new HashMap<>();//存储直播流发送端口通道信息Tcp
    public static Map<String, UDPIpAndPort> ipAndPortMapUdp = new HashMap<>();//存储直播流发送端口通道信息Tcp

    public static Map<String,HistoryMessage> historyMessageMap = new HashMap<>();//存储回放流发送端口通道信息和会话建立信息

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
               //根据有无sdp消息判断属于哪次ack消息
               if(sipMessageEvent.getMessage().getMessageContent() != null){
                   handleAckContainSdp(sipMessageEvent);
               }else{
                   handleAckNoSdp(sipMessageEvent);
               }
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
       }

    }

    //处理包含sdp　ack 消息

    /**
     * 确认流发送者是否正确
     * @param sipMessageEvent
     */
    public void handleAckContainSdp(SipMessageEvent sipMessageEvent) throws UnsupportedEncodingException {
        //根据callId和tag判断会话是否存在
        SIPMessage message = sipMessageEvent.getMessage();
        SIPResponse sipResponse;
        String callId = message.getCallId().getCallId();
        if((sipResponse = sipResponseMap.get(message.getCallId().getCallId())) == null){
            //返回会话不存在信息
            return;
        }
        System.out.println(message);
        System.out.println(sipResponse);
        if(!sipResponse.getToTag().equals(message.getToTag()) && sipResponse.getFromTag().equals(message.getFromTag())){
            //返回会话不存在信息
            return;
        }

        //获取媒体发送发送端口ip
        String sdp = message.getMessageContent();
        String ip = null;//媒体发送者ip
        String port = null;//媒体发送者端口
        String s = parseSDP(sdp, ip, port);
        String[] split = s.split(":");
        ip = split[0];
        port = split[1];
        //解析ｓｄｐ获取cameraCode;
        String cameraCode = parseSDPGetCode(sdp);
        //通过cameraCode存储ip信息
        ipMap.put(callId,s);

        //根据摄像头ip 去取存入的数据
         DataSource dataInfo = PortSingleton.getInstance().getUseSecCodeDataMap().get(cameraCode);
     if(dataInfo==null){
        //如果没有
        return;
       }
      //第二次  存code 作为键  数据
       dataInfo.setSourceIp(ip);
       dataInfo.setSourcePort(Integer.parseInt(port));
       PortSingleton.getInstance().getUseSecCodeDataMap().put(cameraCode,dataInfo);

        //摄像头的ipport为key  存map
        String cameraIpAndPortKey=ip+port;
        PortSingleton.getInstance().getUseSecIpPortDataMap().put(cameraIpAndPortKey,dataInfo);


        //根据cameraId获取接收流port+ip信息
        Map<Integer, String> receiveMap = PortSingleton.getInstance().usedStreamReceiveMap.get(cameraCode);
        //绑定媒体发送者发流port+ip 和流媒体服务器接收流port+ip信息
        PortSingleton.getInstance().getUnUseMapForStreamReceiveBind(Integer.parseInt(port),ip,receiveMap);
    }
    //处理不包含sdp ack消息

    /**
     * 将流发送到指定端口，处理callId，判断callId存在继续，不存在返回会话不存在
     * @param sipMessageEvent
     */
    public void handleAckNoSdp(SipMessageEvent sipMessageEvent){
        //判断回放请求还是直播请求，根据callId获取的值来判断
        HistoryMessage historyMessage = historyMessageMap.get(sipMessageEvent.getMessage().getCallId().getCallId());
        if(historyMessage != null){
            VideoVO vo = historyMessage.getVideoVO();
            while(!historyMessage.isB() && vo == null){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //发送回放数据
            videoService.send(historyMessage);
        }else{
            //得到cameraIp，设置push为true
            SIPResponse sipResponse = sipResponseMap.get(sipMessageEvent.getMessage().getCallId().getCallId());
            String subject = sipResponse.getHeader("Subject").toString();
            String substring = subject.substring(subject.indexOf(":") + 2);
            String cameraCode = substring.substring(0, substring.indexOf(":"));
            //通过cameraCode获取cameraIP
            String s = ipMap.get(sipMessageEvent.getMessage().getCallId().getCallId());
            //通过cameraCode获取cameraIP+port
            //根据cameraIp获取流传输对象
//            IpAndPort ipAndPort = ipAndPortMapTcp.get(s);
//            ipAndPort.setPush(true);
            UDPIpAndPort udpIpAndPort = ipAndPortMapUdp.get(s);
            udpIpAndPort.setPush(true);
        }
    }


    //处理不包含sdp的Invite信令请求消息，第一次获取接收数据端口
    public void handNoSdp(SipMessageEvent sipMessageEvent){
          //用于存连接信息
         DataInfo dataInfo=new DataInfo();


        // TODO 解析 SDP 取出 对方端口 以及 IP
        //根据Subject获取cameraCode
        String subject = sipMessageEvent.getMessage().getHeader("Subject").toString();
        String substring = subject.substring(subject.indexOf(":") + 2);
        String cameraCode = substring.substring(0, substring.indexOf(":"));
        //将流接收地址端口和camera编码进行绑定

        String unUseMap = PortSingleton.getInstance().getRecIpAndPort(cameraCode);
        System.out.println("---------------"+PortSingleton.getInstance().getUnUsedList().size());
        String[] split = unUseMap.split(":");
        //获取流接收端口和ip地址,这里暂时写死,
        SIPRequest message = (SIPRequest) sipMessageEvent.getMessage();
        String sdp = new SDP().getV(ParseUtil.parseMessage(message.getTo().getAddress().toString()),
        split[0],Integer.parseInt(split[1]),0,0);//构建ok消息
//      String sdp = new SDP().getV(ParseUtil.parseMessage(message.getTo().getAddress().toString()),
//      streamＲecConfig.getHost(),streamＲecConfig.getTcpPort(),0,0);
        SIPResponse response = message.createResponse(200);
        response.setToTag(Generate.getTag());
        response.setMessageContent(sdp.getBytes());
        System.out.println(response);
        sipMessageEvent.getConnection().send(response);
        response.addHeader(message.getHeader("Subject"));
          //获取到接收端的IP 断开 并存入
            String receiveIp=split[0];
            Integer receivePort=Integer.parseInt(split[1]);

        sipResponseMap.put(message.getCallId().getCallId(),response);
    }
        //处理包含sdp的Invite信令请求消息
    public void handleContainSdp(SipMessageEvent sipMessageEvent) throws UnsupportedEncodingException {
        // TODO 解析 SDP 取出 对方端口 以及 IP
        //第二次指令，我会给你数据接收端口地址，你回我netty的数据发送端口地址
        SIPRequest message = (SIPRequest) sipMessageEvent.getMessage();
        String ip = null;
        String port = null;
        String oldSdp = message.getMessageContent();
        String s = parseSDP(oldSdp, ip, port);
        String[] split = s.split(":");
        //发送端目标IP port
        ip = split[0];
        port = split[1];
        String []sdpArray = oldSdp.split("\r\n");
        for(String str:sdpArray){
            if(!str.startsWith("t="))continue;
            if("t=0 0".equals(str)){//说明是直播,直播逻辑处理
                //获取媒体发送者iｐ,根据媒体发送者设备编号获取ｉｐ
                System.out.println(message.getHeader("Subject"));
                String subject = message.getHeader("Subject").toString();
                String substring = subject.substring(subject.indexOf(":") + 2);
                String cameraCode = substring.substring(0, substring.indexOf(":"));
                String cameraIp = ipMap.get(message.getCallId().getCallId());
                //启动netty线程
                //tcp传输数据
//                NettyCustomerClient nettyCustomerClient = new NettyCustomerClient(ip, Integer.parseInt(port));
//                nettyCustomerClient.run();
//                System.out.println(nettyCustomerClient.getIpAndPort());
//                ipAndPortMap.put(cameraIp,nettyCustomerClient.getIpAndPort());
                //udp传输数据
                Integer outPort = PortSingleton.getInstance().getOutPort();
                if(outPort != null){

                    Channel outChannel = PortSingleton.getInstance().getOutChanner(outPort);
                    //根据ip存储流传输通道和传输端口信息
                     UDPIpAndPort udpIpAndPort = new UDPIpAndPort(ip, Integer.valueOf(port), streamConfig.getIp(), outPort, outChannel);
                    //已使用端口  设置阀值  都是new  可以不用
                    udpIpAndPort.setThreshold(0);
                    ipAndPortMapUdp.put(cameraIp,udpIpAndPort);
                    //将保存信息存于portSingle
                    PortSingleton.getInstance().getUseSendData().put(cameraIp,udpIpAndPort);
                    //根据ip存储流传输通道和传输端口信息cameraIpPort
                 //   ipAndPortMapUdp.put(cameraIpPort,new UDPIpAndPort(ip,Integer.valueOf(port),streamConfig.getIp(),outPort, outChannel));

                    PortSingleton.getInstance().removeOutPort(outPort);
                }else{//端口使用完毕
                    return;
                }



                //流发送端口和发送地址,这里调用方法获取端口
                String sdp = new SDP().getV(streamConfig.getCode(),streamConfig.getIp(),outPort,0,0);//构建ok消息

                SIPResponse response = message.createResponse(200);
                response.setToTag(Generate.getTag());
                response.addHeader("Content-Length: "+sdp.length());
                response.setMessageContent(sdp.getBytes());
                sipMessageEvent.getConnection().send(response);
                break;
            }else{//回放逻辑处理
                //获取回放开始时间和结束时间
                long startTime = Long.parseLong(str.substring(2,str.indexOf(" ")).trim());
                long endTime = Long.parseLong(str.substring(str.indexOf(" ")).trim());
//                TcpHistoryClient tcpHistoryClient = new TcpHistoryClient(ip, Integer.valueOf(port));
//                tcpHistoryClient.run();
//                IpAndPort ipAndPort = tcpHistoryClient.getIpAndPort();
                //根据callId存储会话信息和文件发送端口信息
                HistoryMessage historyMessage = new HistoryMessage();
                Integer outPort = PortSingleton.getInstance().getOutPort();
                if(outPort != null){
                    Channel outChanner = PortSingleton.getInstance().getOutChanner(outPort);
                    //根据ip存储流传输通道和传输端口信息
                     UDPIpAndPort udpIpAndPort = new UDPIpAndPort(ip, Integer.valueOf(port), streamConfig.getIp(), outPort, outChanner);
                    historyMessage.setUdpIpAndPort(udpIpAndPort);
                    //将保存信息存于portSingle
                    String cameraIp = ipMap.get(message.getCallId().getCallId());
                     //已使用端口  设置阀值
                    udpIpAndPort.setThreshold(0);
                    PortSingleton.getInstance().getUseSendData().put(cameraIp,udpIpAndPort);

                    PortSingleton.getInstance().removeOutPort(outPort);
                }else{//端口使用完毕
                    return;
                }

//                historyMessage.setIpAndPort(ipAndPort);
                String sdp = new SDP().getV(streamConfig.getCode(),streamConfig.getIp(),outPort,startTime,endTime);//构建ok消息
                SIPResponse response = message.createResponse(200);
                response.setToTag(Generate.getTag());
                response.addHeader("Content-Length: "+sdp.length());
                response.setMessageContent(sdp.getBytes());
                historyMessage.setSipResponse(response);
                historyMessageMap.put(message.getCallId().getCallId(),historyMessage);
                sipMessageEvent.getConnection().send(response);
                //异步实现文件查找
                CompletableFuture.runAsync(() -> {
                    Instant instant = Instant.ofEpochMilli(startTime);
                    ZoneId zone = ZoneId.systemDefault();
                    VideoVO videoVO = videoService.getVideoVO("192.168.6.93", LocalDateTime.ofInstant(instant, zone));
                    historyMessage.setVideoVO(videoVO);
                    historyMessage.setB(true);
                    historyMessageMap.put(message.getCallId().getCallId(),historyMessage);
                });
            }
        }

        //解析流发送地址，暂时未做tag和callId分析
    }

    public String parseSDP(String sdp,String ip,String port){
        String[] split = sdp.split("\r\n");
        for(String str:split){
            if(str.startsWith("c=IN")){
                ip=str.substring(str.lastIndexOf(" ")).trim();
                continue;
            }
            if(str.startsWith("m=video")){
                String substring = str.substring(str.indexOf(" ")+1);
                port = substring.substring(0,substring.indexOf(" ")).trim();
            }
        }
        return ip+":"+port ;
    }
    private String parseSDPGetCode(String sdp) {
        String[] split = sdp.split("\n");
        for(String str:split){
            if(str.startsWith("o")){
                return str.substring(str.indexOf("=")+1,str.indexOf(" "));
            }
        }
        return null;
    }
}
