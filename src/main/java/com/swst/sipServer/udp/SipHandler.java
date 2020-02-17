package com.swst.sipServer.udp;

import com.swst.rtphandle.RtpH264Parse;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import com.swst.utils.WriteMessageToSIP;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: fregun
 * @Date: 19-11-4 11:56
 * @Description: 向sip服务器进行注册
 */
@Component
public class SipHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {

//        if(WriteMessageToSIP.ctx == null){
//            WriteMessageToSIP.ctx = channelHandlerContext;
//        }
//        String s = datagramPacket.content().toString(CharsetUtil.UTF_8);
//        Map<String,String> headers = new HashMap<String, String>();
//        String[] split = s.split("\r\n");
//        for (int i=1;i<split.length;i++) {
//            String[] split1 = split[i].split(": ");
//            if(split1.length>1)
//            headers.put(split1[0].trim(),split1[1].trim());
//        }
//        System.out.println(s);
////        RtpH264Parse.sb.append(s);
////        RtpH264Parse.sb.append("a=fmtp:98 sprop-parameter-sets=");
//        if(s.contains("CSeq: 1")){
//            String res = responseHandle1(headers);
//            System.out.println(res);
//            InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94",5060);
//            DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
//            channelHandlerContext.writeAndFlush(datagramPacket1);
//        }else if("2 REGISTER".equals(headers.get("CSeq").trim())){
//            String res = responseHandle2(headers);
//            System.out.println(res);
//            InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94",5060);
//            DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
//            channelHandlerContext.writeAndFlush(datagramPacket1);
//        }else if(split[0].contains("MESSAGE sip")){
//            System.out.println("认证成功");
//            if(b) {
//                WriteMessageToSIP.Call_ID = headers.get("Call-ID");
//                String res =
//                        "MESSAGE sip:C5-07-D2@192.168.6.97:5060 SIP/2.0" + "\n" +
//                                "To: <sip:C5-07-D2@192.168.6.97:5060>" + "\n" +
//                                "Content-Length: " + Constants.enlarge.length() + "\n" +
//                                "CSeq: " + headers.get("CSeq") + "\n" +
//                                "Call-ID: " + headers.get("Call-ID") + "\n" +
//                                "Via: " + headers.get("Via") + "\n" +
//                                "From: <sip:C5-07-D2@192.168.6.97:5060>"+ "\n" +
//                                "Content-Type: Application/MANSCDP+xml" + "\n" +
//                                "Max-Forwards: 70\n" + "\n" +
//                                Constants.enlarge;
//                String res2 =
//                        "MESSAGE sip:C5-07-D2@192.168.6.97:5060 SIP/2.0" + "\n" +
//                                "To: <sip:C5-07-D2@192.168.6.97:5060>" + "\n" +
//                                "Content-Length: " + Constants.stop.length() + "\n" +
//                                "CSeq: " + headers.get("CSeq") + "\n" +
//                                "Call-ID: " + headers.get("Call-ID") + "\n" +
//                                "Via: " + headers.get("Via") + "\n" +
//                                "From: <sip:C5-07-D2@192.168.6.97:5060>"+ "\n" +
//                                "Content-Type: Application/MANSCDP+xml" + "\n" +
//                                "Max-Forwards: 70\n" + "\n" +
//                                Constants.stop;
//
//
//                InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94", 5060);
//                System.out.println(res);
//                DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
//                channelHandlerContext.writeAndFlush(datagramPacket1);
//                DatagramPacket datagramPacket2 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
//                channelHandlerContext.writeAndFlush(datagramPacket2);
//               Timer.b = true;
//                b=false;
//                Thread.sleep(5000);
//            }
//
//        }else if(split[0].contains("SIP/2.0 200 OK")){
//            Timer.b=false;
////            System.out.println(headers.get("Content-Length").length());
////            if(headers.get("Content-Length").equals("0")){
////                WriteMessageToSIP.to=headers.get("To");
////                WriteMessageToSIP.CSeq = headers.get("CSeq");
////                WriteMessageToSIP.Call_ID = headers.get("Call-ID");
////                WriteMessageToSIP.Via = headers.get("Via");
////                WriteMessageToSIP.From = headers.get("From");
////                WriteMessageToSIP.user = headers.get("User-Agent");
////                Timer.b = true;
////            }
////            else{
////                System.out.println("invite返回: "+s);
////            }
//            if (headers.get("CSeq").contains("INVITE")){
//
//                if(a){
//                    Timer.b=false;
////                    String res =
////                            "ACK sip:C5-07-D2@192.168.6.96:5060 SIP/2.0" + "\n" +
////                                    "To: "+headers.get("To")+"\n"+
////                                    "Content-Length: "+0+"\n"+
////                                    "CSeq: 20 ACK"+"\n"+
////                                    "Call-ID: "+ headers.get("Call-ID")+"\n"+
////                                    "Via: "+headers.get("Via")+"\n"+
////                                    "From: "+headers.get("From")+"\n"+
////                                    "Max-Forwards: 70" + "\r\n";//注意这里的\r\n都要，否则传不到sip客户端
//                    String res =
//                            "ACK sip:C5-07-D2@192.168.6.94 SIP/2.0" + "\n" +
//                                    "To: "+headers.get("To")+"\n"+
//                                    "Content-Length: "+0+"\n"+
//                                    "CSeq: 20 ACK"+"\n"+
//                                    "Call-ID: "+ headers.get("Call-ID")+"\n"+
//                                    "Via: "+headers.get("Via")+"\n"+
//                                    "From: "+headers.get("From")+"\n"+
//                                    "Max-Forwards: 70" + "\r\n";//注意这里的\r\n都要，否则传不到sip客户端
//                    InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94",5060);
//                    DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
//                    channelHandlerContext.writeAndFlush(datagramPacket1);
//                    a = false;
//                }
//
//            }
//        }

    }

    /**
     * 注册校验
     * @param headers 第二次请求注册头内容
     */

    private String responseHandle2(Map<String, String> headers) throws NoSuchAlgorithmException {
        String[] authorizations = headers.get("Authorization").split(",");
       //得到加密参数和加密结果
        String [] authorizationParams = new String[5];
        int i=0;
        for (String str:authorizations) {
            if(i==5){
                break;
            }
            String substring = str.substring(str.indexOf("\"") + 1, str.lastIndexOf("\""));
            authorizationParams[i]=substring;
            i++;
        }
        //加密规则 1） HASH1=MD5(username:realm:password)
        //
        //        2） HASH2=MD5(method:uri)
        //
        //        3）Response=MD5(HA1:nonce:HA2)
        MessageDigest digest = MessageDigest.getInstance("MD5");
        digest.update((authorizationParams[0]+":"+authorizationParams[1]+":"+"123456").getBytes());
        String s = new BigInteger(1, digest.digest()).toString(16);
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update((authorizationParams[0]+":"+authorizationParams[1]+":"+"123456").getBytes());
        String s3 = new BigInteger(1, digest.digest()).toString(16);
        digest.update(("REGISTER"+":"+authorizationParams[3]).getBytes());
        String s1 = new BigInteger(1, digest.digest()).toString(16);

        digest.update((s+":"+authorizationParams[2]+":"+s1).getBytes());
        String s2 = new BigInteger(1, digest.digest()).toString(16);
        if (!s2.equals(authorizationParams[4])){
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SIP/2.0 200 OK\r\n");
        sb.append("Contact: "+headers.get("Contact")+"\r\n");
        sb.append("To: "+headers.get("To")+"\r\n");
        sb.append("Content-Length: "+headers.get("Content-Length")+"\r\n");
        sb.append("CSeq: "+headers.get("CSeq")+"\r\n");
        sb.append("Call-ID: "+headers.get("Call-ID")+"\r\n");
        sb.append("From: "+headers.get("From")+"\r\n");
        sb.append("Via: "+headers.get("Via")+"\r\n");
        sb.append("Date: "+new Date()+"\r\n");
        sb.append("Expires: "+headers.get("Expires")+"\r\n");
        return sb.toString();
    }

    /**
     * 注册请求
     * @param headers 第一次请求注册校验头内容
     */
    private String responseHandle1(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        sb.append("SIP/2.0 401 Unauthorized\r\n");
        sb.append("To: "+headers.get("To")+"\r\n");
        sb.append("Content-Length: "+headers.get("Content-Length")+"\r\n");
        sb.append("CSeq: "+headers.get("CSeq")+"\r\n");
        sb.append("Call-ID: "+headers.get("Call-ID")+"\r\n");
        sb.append("From: "+headers.get("From")+"\r\n");
        sb.append("Via: "+headers.get("Via")+"\r\n");
        sb.append("WWW-Authenticate: Digest realm=\"64010000\",nonce=\"6fe9ba44a76be22a\"\r\n");
        return sb.toString();
    }
}


