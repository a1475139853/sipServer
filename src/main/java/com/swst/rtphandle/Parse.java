package com.swst.rtphandle;

/**
 * @Auther: fregun
 * @Date: 19-12-3 16:43
 * @Description: parse rtp and PS stream
 */
public class Parse {
    public static final int RTP_LENGTH = 12;//rtp包头长度
    public static int lastSeqNumber3 = -1;//上一包的序列号前八位，判断包的连续性
    public static int lastSeqNumber4 = -1;//上一包的序列号后八位，判断包的连续性
    public static int m_pt = -1;//负载类型
    public static int seq3 = -1;//前八位seqNumber
    public static int seq4 = -1;//后八位seqNumber
    public static boolean wrap = false;
    public static int m = 1;
    public static final int I = 0x7c;
    public static final int P = 0x5c;
    /**
     * 处理rtp请求头
     * @param data 流数据
     */
    public static void parseRtp(byte[]data){
        StringBuilder sb = new StringBuilder(data.length);
        StringBuilder seqNumber = new StringBuilder(16);
      for(int i=0;i<data.length;i++){
          String s = Integer.toHexString(0xFF & data[i]);
          if(i==1){
              if(Integer.parseInt(s,16)>>7 == 1){//1标志一帧视频的结束，一帧音频的开始,下一帧数据需要处理PS头和PS描述
                  m=1;
              }else{
                  m=0;
                  int num = Integer.parseInt(Integer.toBinaryString(data[i]<<1 >>1), 2);
                  if(num == 96){
                      m_pt = 96;
                  }
              }
          }
          if(i==2){
              int i1 = Integer.parseInt(s, 16);
              seq3 = i1;
              System.out.println("lastSeqNumber3:"+lastSeqNumber3);
              System.out.println("seqNum3:"+i1);
          }
          if(i==3){
              int i1 = Integer.parseInt(s, 16);
              seq4 = i1;
              System.out.println("lastSeqNumber4:"+lastSeqNumber4);
              System.out.println("seqNum4:"+i1);
             //包连续性判断

//              if(wrapJudge(lastSeqNumber3, lastSeqNumber4, seq3, seq4)){
//                  lastSeqNumber3 = seq3;
//                  lastSeqNumber4 = seq4;
//                  wrap = true;//告诉后续当连续包处理，false存入内存，直到包连续为止
//              }
          }

      }
    }

    /**
     *
     * @param lastSeqNumber3
     * @param lastSeqNumber4
     * @param seq3
     * @param seq4
     * @return 包连续返回true，非连续返回false
     */
    public static Boolean wrapJudge(int lastSeqNumber3,int lastSeqNumber4,int seq3,int seq4){
        if (lastSeqNumber3 == -1 || lastSeqNumber4 == -1){//第一次收到包
            lastSeqNumber3 = seq3;
            lastSeqNumber4 = seq4;
            return true;
        }
           if(seq3 == lastSeqNumber3 && seq4 - lastSeqNumber4==1){
               return true;
           }
           if(seq3 - lastSeqNumber3 == 1 && lastSeqNumber4 - seq4 == 255){
               return true;
           }
           return false;
    }
}
