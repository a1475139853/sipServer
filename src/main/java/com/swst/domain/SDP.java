package com.swst.domain;

/**
 * @Auther: fregun
 * @Date: 19-12-28 17:52
 * @Description: 封装发送或者接收端口sdp文件信息
 */
public class SDP {
    public static final String sdptop = "v=0\n" +
            "o=C5-07-D2 0 0 IN IP4 192.168.6.201\n" +
            "s=Play\n" +
            "c=IN IP4 192.168.6.201\n" +
            "t=0 0\n" +
            "m=video ";
    public static final String sdpBottom = "RTP/AVP 98\n" +
            "a=recvonly\n" +
//            "a=rtpmap:96 PS/90000\n" +
            "a=rtpmap:98 H264/90000\n" ;
    private String version = "0";
    private String internationCode;//流媒体服务器国际编码
    private String streamIp;//流媒体服务器接收或者发送ip
    private int streamPortV;//流媒体服务器video接收或者发端口
    private int streamPortA;//流媒体服务器audio接收或者发送端口
    private long startTime;//请求流开始时间
    private long endTime;//请求流结束时间
    private String s = "Play";//回话名

    /**
     *
     * @param internationCode 流媒体服务器国际编码
     * @param streamIp 流媒体服务器接收或者发送ip
     * @param streamPortV 流媒体服务器video接收或者发端口
     * @param startTime 请求流开始时间
     * @param endTime 请求流结束时间
     * @return 接收或者发送Video的sdp文件
     */
    public String getV(String internationCode,String streamIp,int streamPortV,long startTime,long endTime){
        if(internationCode == null){
            throw new RuntimeException("internationCode can't be null");
        }
        if(streamIp == null){
            throw new RuntimeException("streamIp can't be null");
        }
        if(streamPortV < 0){
            throw new RuntimeException("streamPortV is incorrect");
        }
        if(startTime > endTime){
            throw new RuntimeException("startTime can't be later than endTime");
        }
        if(startTime <0 || endTime <0){
            throw new RuntimeException("startTime or endTime must be large than 0");
        }
        return "v="+version+"\n"
                +"o="+internationCode+" 0 0 IN IP4 "+streamIp+"\n"
                +"s=" +s+"\n"
                +"c=IN IP4 "+streamIp+"\n"
                +"t="+startTime+" "+endTime+"\n"
                +"m=video"+streamPortV+"RTP/AVP 98\n"
                +"a=recvonly\n"
                +"a=rtmp:98 H264/90000\n";
    }

    public String getA(String internationCode,String streamIp,int streamPortA,long startTime,long endTime){
        if(internationCode == null){
            throw new RuntimeException("internationCode can't be null");
        }
        if(streamIp == null){
            throw new RuntimeException("streamIp can't be null");
        }
        if(streamPortA < 0){
            throw new RuntimeException("streamPortA is incorrect");
        }
        if(startTime > endTime){
            throw new RuntimeException("startTime can't be later than endTime");
        }
        if(startTime <0 || endTime <0){
            throw new RuntimeException("startTime or endTime must be large than 0");
        }
        return "v="+version+"\n"
                +"o="+internationCode+" 0 0 IN IP4 "+streamIp+"\n"
                +"s=" +s+"\n"
                +"c=IN IP4 "+streamIp+"\n"
                +"t="+startTime+" "+endTime+"\n"
                +"m=audio"+streamPortV+"RTP/AVP 98\n"
                +"a=recvonly\n"
                +"a=rtmp:98 H264/90000\n";
    }
    public String getVAndA(String internationCode,String streamIp,int streamPortA,int streamPortV,long startTime,long endTime){
        if(internationCode == null){
            throw new RuntimeException("internationCode can't be null");
        }
        if(streamIp == null){
            throw new RuntimeException("streamIp can't be null");
        }
        if(streamPortV < 0){
            throw new RuntimeException("streamPortV is incorrect");
        }
        if(streamPortA < 0){
            throw new RuntimeException("streamPortA is incorrect");
        }
        if(startTime > endTime){
            throw new RuntimeException("startTime can't be later than endTime");
        }
        if(startTime <0 || endTime <0){
            throw new RuntimeException("startTime or endTime must be large than 0");
        }
        return "v="+version+"\n"
                +"o="+internationCode+" 0 0 IN IP4 "+streamIp+"\n"
                +"s=" +s+"\n"
                +"c=IN IP4 "+streamIp+"\n"
                +"t="+startTime+" "+endTime+"\n"
                +"m=video"+streamPortV+"RTP/AVP 98\n"
                +"a=recvonly\n"
                +"a=rtmp:98 H264/90000\n"
                +"m=audio"+streamPortA+"RTP/AVP 98\n"
                +"a=recvonly\n"
                +"a=rtmp:98 H264/90000\n";
    }

}
