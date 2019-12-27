package com.swst.sipServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import com.swst.utils.WriteMessageToSIP;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @Auther: fregun
 * @Date: 19-11-8 09:53
 * @Description:
 */
public class Timer implements Runnable{
    public static boolean b = false;

    public  void startTask(ChannelHandlerContext ctx,String res){
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.6.94", 5060);
        DatagramPacket datagramPacket1 = new DatagramPacket(Unpooled.wrappedBuffer(res.getBytes()), inetSocketAddress);
        System.out.println(res);
        ctx.writeAndFlush(datagramPacket1);
    }

    public  void tast(){
        if(b){
            System.out.println("开始发送");
            String res =
                    "INVITE sip:C5-07-D2@192.168.6.94 SIP/2.0" + "\n" +
                            "To: <sip:C5-07-D2@192.168.6.94>\n"+
                            "Content-Length: "+ Constants.actureTime.length()+"\n"+
                            "Contact: <sip:iccsid@192.168.6.201:5060>"+"\n"+
                            "CSeq: 20 INVITE"+"\n"+
                            "Call-ID: "+ UUID.randomUUID().toString().replace("-","")+"\n"+
                            "Via: SIP/2.0/UDP 192.168.6.201:5060;rport;branch=z9hG4bK1730298036"+"\n"+
                            "From: <sip:iccsid@192.168.6.201>;tag=1087979339\n"+
                            "Content-Type: Application/SDP"+"\n"+
                            "Subject: C5-07-D2:0,iccsid:0"+"\n"+
                            "Max-Forwards: 70" + "\n" +"\n"+Constants.actureTime;

            startTask(WriteMessageToSIP.ctx,res);
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        System.out.println("定时任务开始");
        while(true){
            tast();
        }

    }
}
