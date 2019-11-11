package com.swst.sipServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import utils.WriteMessageToSIP;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther: fregun
 * @Date: 19-11-4 11:56
 * @Description:
 */
public class SipHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    boolean b = true;
    boolean a = true;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket) throws Exception {
        if(WriteMessageToSIP.ctx == null){
            WriteMessageToSIP.ctx = channelHandlerContext;
        }
        String s = datagramPacket.content().toString(CharsetUtil.UTF_8);
        Map<String,String> headers = new HashMap<String, String>();
        String[] split = s.split("\r\n");
        for (int i=1;i<split.length;i++) {
            String[] split1 = split[i].split(": ");
            if(split1.length>1)
            headers.put(split1[0].trim(),split1[1].trim());
        }
        System.out.println(s);
        if(s.contains("CSeq: 1")){
            String res = responseHandle1(headers);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94",5060);
            DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
            channelHandlerContext.writeAndFlush(datagramPacket1);
        }else if("2 REGISTER".equals(headers.get("CSeq").trim())){
            String res = responseHandle2(headers);
            InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94",5060);
            DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
            channelHandlerContext.writeAndFlush(datagramPacket1);
        }else if(split[0].contains("MESSAGE sip")){
            System.out.println("认证成功");
            if(b) {
                WriteMessageToSIP.Call_ID = headers.get("Call-ID");
//                String res =
//                        "MESSAGE sip:iccsid@192.168.6.201:5060 SIP/2.0" + "\n" +
//                                "To: <sip:iccsid@192.168.6.201:5060>" + "\n" +
//                                "Content-Length: " + Constants.content.length() + "\n" +
//                                "CSeq: " + headers.get("CSeq") + "\n" +
//                                "Call-ID: " + headers.get("Call-ID") + "\n" +
//                                "Via: " + headers.get("Via") + "\n" +
//                                "From: " + headers.get("From") + "\n" +
//                                "Content-Type: Application/MANSCDP+xml" + "\n" +
//                                "Max-Forwards: 70\n" + "\n" +
//                                Constants.content;
//
//                InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.201", 5061);
//                DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer("请求Invite".getBytes()), inetSocketAddress);
//                channelHandlerContext.writeAndFlush(datagramPacket1);
                Timer.b = true;
                b=false;
                Thread.sleep(5000);
            }

        }else if(split[0].contains("SIP/2.0 200 OK")){
//            System.out.println(headers.get("Content-Length").length());
//            if(headers.get("Content-Length").equals("0")){
//                WriteMessageToSIP.to=headers.get("To");
//                WriteMessageToSIP.CSeq = headers.get("CSeq");
//                WriteMessageToSIP.Call_ID = headers.get("Call-ID");
//                WriteMessageToSIP.Via = headers.get("Via");
//                WriteMessageToSIP.From = headers.get("From");
//                WriteMessageToSIP.user = headers.get("User-Agent");
//                Timer.b = true;
//            }
//            else{
//                System.out.println("invite返回: "+s);
//            }
            if (headers.get("CSeq").contains("INVITE5")){
                if(a){
                    Timer.b=false;
                    String res =
                            "ACK sip:C5-07-D2@192.168.6.94:5060 SIP/2.0" + "\n" +
                                    "To: <sip:C5-07-D2@192.168.6.94:5060>\n"+
                                    "Content-Length: "+0+"\n"+
                                    "CSeq: 1 ACK"+"\n"+
                                    "Call-ID: "+ UUID.randomUUID().toString().replace("-","")+"\n"+
                                    "Via: SIP/2.0/UDP 192.168.6.201:5060;rport;branch=z9hG4bK1730298036"+"\n"+
                                    "From: <sip:iccsid@192.168.6.201:5060>;tag=1087979339\n"+
                                    "Content-Type: application/sdp"+"\n"+
                                    "Max-Forwards: 70" + "\n";
                    InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94",5060);
                    DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
                    channelHandlerContext.writeAndFlush(datagramPacket1);
                    a = false;
                }
            }
        }

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


