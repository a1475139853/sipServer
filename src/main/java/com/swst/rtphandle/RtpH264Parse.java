package com.swst.rtphandle;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * @Auther: fregun
 * @Date: 19-12-6 15:05
 * @Description: 处理RTP负载H264裸流
 */
public class RtpH264Parse {

    //不解析H264数据,判断类型
    public static final int SPS = 0x67;//SPS
    public static final int PPS = 0x68;//PPS
    public static final int SEI = 0x66;//SEI
    public static final int IFrame = 0x65;//I Frame
    public static final int PFrame = 0x41;//P Frame
    //解析Ｈ２６４数据，判断类型
    public static final int FUINDICATOR_I = 0x7c;// I帧
    public static final int FUINDICATOR_P = 0x5c;// Ｐ帧
    public static final byte[]header = {00,00,00,01};
    StringBuilder sb = new StringBuilder();

    /**
     * 解析H264数据
     * @param bytes 基于UDP传输的h264裸流传输，可能单NAUL，多NAUL和UAs一帧数据分多个rtp包发送
     * 这里不处理包连续性问题，传进来的所有包当做连续包处理，包连续性处理在调用类
     */
    public static void handleNalHeader(byte[] bytes) throws IOException {
        switch (bytes[12]&0xff){
            case SPS : StreamSave.handleStream(header,false);StreamSave.handleStream(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
            case PPS : StreamSave.handleStream(header,false);StreamSave.handleStream(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
            case SEI : StreamSave.handleStream(header,false);StreamSave.handleStream(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
            default : getNalHeader(Arrays.copyOfRange(bytes,12,bytes.length));
        }
    }

    public static void getNalHeader(byte[]bytes) throws IOException {
        DecimalFormat df = new DecimalFormat("00000000");
        String fuIndicator = Integer.toBinaryString(bytes[0]&0xff);
        String fuHeader = Integer.toBinaryString(bytes[1]&0xff);
        while(fuHeader.length()<8){
            fuHeader = "0"+fuHeader;
        }
        while(fuIndicator.length()<8){
            fuIndicator = "0"+fuIndicator;
        }
       if(bytes[0] == FUINDICATOR_I){
           //判断当前数据是开始还是结尾
           if(fuHeader.startsWith("10")){
               //分片开始，获取ｎａｕｌ头信息
               StreamSave.handleStream(header,false);
               String naulHeader = fuIndicator.substring(0,3)+fuHeader.substring(3,fuHeader.length());
               //生成１０进制
               bytes[1] = Byte.parseByte(naulHeader, 2);
               StreamSave.handleStream(Arrays.copyOfRange(bytes,1,bytes.length),false);

           }else if(fuHeader.startsWith("01")){
               //分片结束
               StreamSave.handleStream(Arrays.copyOfRange(bytes,2,bytes.length),true);
           }else{
               StreamSave.handleStream(Arrays.copyOfRange(bytes,2,bytes.length),false);
           }

       }

    }
}
