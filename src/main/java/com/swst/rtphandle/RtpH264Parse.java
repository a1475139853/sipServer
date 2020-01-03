package com.swst.rtphandle;

import sun.nio.cs.ext.GBK;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Auther: fregun
 * @Date: 19-12-6 15:05
 * @Description: 处理RTP负载H264裸流
 */
public class RtpH264Parse {

    //不解析H264数据,判断类型
    public final int SPS = 0x67;//SPS
    public final int PPS = 0x68;//PPS
    public final int SEI = 0x66;//SEI
    public final int IFrame = 0x65;//I Frame
    public final int PFrame = 0x41;//P Frame
    //解析Ｈ２６４数据，判断类型
    public final int FUINDICATOR_I = 0x7c;// I帧
    public final int FUINDICATOR_P = 0x5c;// Ｐ帧
    public final byte[]header = {00,00,00,01};
    private StreamSave streamSave = new StreamSave();
//    public static StringBuilder sb = new StringBuilder();
//    static boolean isFirst;
    /**
     * 解析H264数据
     * @param bytes 基于UDP传输的h264裸流传输，可能单NAUL，多NAUL和UAs一帧数据分多个rtp包发送
     * 这里不处理包连续性问题，传进来的所有包当做连续包处理，包连续性处理在调用类
     */
    public void handleNalHeader(byte[] bytes,String ip) throws IOException {
//        if(isFirst){
//            return;
//        }
//        switch ((bytes[12]&0xff)){
//            case SPS:getBase64String(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
//            case PPS:getBase64String(Arrays.copyOfRange(bytes,12,bytes.length),true);break;
//            default:;
//        }
        byte[] bytes1 = Arrays.copyOfRange(bytes, 12, bytes.length);
        byte []byte1 = new byte[header.length+bytes1.length];
        System.arraycopy(header,0,byte1,0,header.length);
        System.arraycopy(bytes1,0,byte1,byte1.length-bytes1.length,bytes1.length);
        int frame = 0 ;
        switch (bytes[12]&0xff){
            case SPS :
                streamSave.handleStream(byte1,false,ip);break;
//            StreamSave.handleStream(header,false);
//            StreamSave.handleStream(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
            case PPS : streamSave.handleStream(byte1,false,ip);break;
//                StreamSave.handleStream(header,false);StreamSave.handleStream(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
            case SEI : streamSave.handleStream(byte1,false,ip);break;
//                StreamSave.handleStream(header,false);StreamSave.handleStream(Arrays.copyOfRange(bytes,12,bytes.length),false);break;
            default : getNalHeader(Arrays.copyOfRange(bytes,12,bytes.length),ip);
        }
    }

    public void getBase64String(byte[]bytes,boolean bool) throws UnsupportedEncodingException {
        String []s2 = new String[bytes.length];
        for (int i=0;i<bytes.length;i++) {
            String s = Integer.toBinaryString(bytes[i] & 0xff);
            while(s.length()<8){
                s = "0"+s;
            }
            s2[i]=s;

        }
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] decode = encoder.encode(bytes);
//        byte[] bytes1 = s.getBytes();
        for (byte bb:decode) {
            System.out.println(bb);
        }
//        sb.append(new String(decode,"UTF-8"));
//        if(bool){
//            sb.append(";");
//            isFirst = true;
//            System.out.println(sb.toString());
//        }else{
//            sb.append(",");
//        }
    }

    public void getNalHeader(byte[]bytes,String ip) throws IOException {
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
//               StreamSave.handleStream(header,false);
               String naulHeader = fuIndicator.substring(0,3)+fuHeader.substring(3,fuHeader.length());
               //生成１０进制
               bytes[1] = Byte.parseByte(naulHeader, 2);
               byte[] bytes2 = Arrays.copyOfRange(bytes, 1, bytes.length);
               byte[]bytes1 = new byte[header.length+bytes2.length];
               System.arraycopy(header,0,bytes1,0,header.length);
               System.arraycopy(bytes2,0,bytes1,bytes1.length-bytes2.length,bytes2.length);
               streamSave.handleStream(bytes1,false,ip);

           }else if(fuHeader.startsWith("01")){
               //分片结束
               streamSave.handleStream(Arrays.copyOfRange(bytes,2,bytes.length),true,ip);
           }else{
               streamSave.handleStream(Arrays.copyOfRange(bytes,2,bytes.length),false,ip);
           }

       }

    }
}
