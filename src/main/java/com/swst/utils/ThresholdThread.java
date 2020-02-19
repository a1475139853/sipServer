package com.swst.utils;

import com.swst.domain.DataInfo;
import com.swst.domain.DataSource;
import com.swst.domain.UDPIpAndPort;
import com.swst.videoRecServer.PortSingleton;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author frosty
 * @description 判断 已使用对象阀值是否到达8   如果大于8  就移除   userMap
 * unUsedList   未使用的接收
 * @date 2020-01-12 12:15:04
 */


public class ThresholdThread implements Runnable {
    Map<String, DataSource> useSecIpPortDataMap = null;
    Map<String, DataSource> useSecCodeDataMap = null;


    private int time;

    public ThresholdThread(int time) {

        this.time = time;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2000);
                useSecCodeDataMap = PortSingleton.getInstance().getUseSecCodeDataMap();
                Map<String, UDPIpAndPort> useSendData = PortSingleton.getInstance().getUseSendData();

              //中间 接收端口
                if (useSecCodeDataMap != null && useSecCodeDataMap.keySet().size() != 0) {


                    useSecIpPortDataMap = PortSingleton.getInstance().getUseSecIpPortDataMap();
                    //需要移除的key
                    List<String> removeList = new ArrayList<>();
        //           unUsedList = PortSingleton.getInstance().getUnUsedList();
                    Set<String> strings = useSecCodeDataMap.keySet();
                    DataSource daIn = null;
                    for (String str : strings) {
                        daIn = useSecCodeDataMap.get(str);
                        String key = daIn.getSourceIp() + daIn.getSourcePort();

                        //  daIn = useCodeDataMap.get(str);
                        if (daIn.getThreshold() < time) {
                            daIn.setThreshold(daIn.getThreshold() + 2);
                            useSecIpPortDataMap.put(key, daIn);
                            useSecCodeDataMap.put(str, daIn);
                        } else {
                            useSecIpPortDataMap.remove(key);
                            //返还过期接收端端口
                            PortSingleton.getInstance().getUnUsedList().add(daIn.getLocalRecPort());
                            removeList.add(str);

                        }
                    }


                    if (removeList.size() > 0) {
                        for (String remove : removeList) {
                            useSecCodeDataMap.remove(remove);
                        }
                    }
                }

                //中间 发送端端口检查
                 if(useSendData!=null&&useSendData.keySet().size()>0){
                      Set<String> strings = useSendData.keySet();
                       List<String> remove =new ArrayList<>();
                      for(String str : strings){
                           UDPIpAndPort udpIpAndPort = useSendData.get(str);
                            if(udpIpAndPort.getThreshold()>8){

                                  remove.add(str);
                                  //归还 超过时间的发送端端口
                                  PortSingleton.getInstance().getUnUsedOutMap().put(udpIpAndPort.getRecPort(),udpIpAndPort.getChannel());
                            }else {
                                udpIpAndPort.setThreshold(udpIpAndPort.getThreshold()+2);
                                PortSingleton.getInstance().getUseSendData().put(str,udpIpAndPort);
                            }

                      }
                     if(remove.size()>0){
                         for (String reString : remove){
                             //移除
                             PortSingleton.getInstance().getUseSendData().remove(reString);
                         }
                     }




                 }



            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

