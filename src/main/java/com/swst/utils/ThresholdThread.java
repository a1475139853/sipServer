package com.swst.utils;

import com.swst.domain.DataInfo;
import com.swst.videoRecServer.PortSingleton;

import java.util.*;

/**
 * @author frosty
 * @description 判断 已使用对象阀值是否到达8   如果大于8  就移除   userMap
 * unUsedList   未使用的接收
 * @date 2020-01-12 12:15:04
 */


public class ThresholdThread implements Runnable {
    Map<String, DataInfo> useIpPortDataMap = null;
    Map<String, DataInfo> useCodeDataMap = null;


    private int time;

    public ThresholdThread(int time) {

        this.time = time;
    }

    @Override
    public void run() {
        while (true) {
            try {
              Thread.sleep(2000);
              useCodeDataMap = PortSingleton.getInstance().getUseCodeDataMap();
            if (useCodeDataMap == null || useCodeDataMap.keySet().size() == 0) {
                continue;
            }
            useIpPortDataMap = PortSingleton.getInstance().getUseIpPortDataMap();
            //需要移除的key
            List<String> removeList = new ArrayList<>();
//           unUsedList = PortSingleton.getInstance().getUnUsedList();
            Set<String> strings = useCodeDataMap.keySet();
            DataInfo daIn = null;
            for (String str : strings) {
                 daIn = useCodeDataMap.get(str);
                String key = daIn.getCameraIp() + daIn.getCameraPort();

              //  daIn = useCodeDataMap.get(str);
                if (daIn.getThreshold() < time) {
                    daIn.setThreshold(daIn.getThreshold() + 2);
                    useIpPortDataMap.put(key, daIn);
                    useCodeDataMap.put(str, daIn);
                } else {
                    useIpPortDataMap.remove(key);
                    removeList.add(str);

                }
            }


            if (removeList.size() > 0) {

                for (String remove : removeList) {
                    Integer receivePort = useCodeDataMap.get(remove).getReceivePort();
                    System.out.println(PortSingleton.getInstance().getUnUsedList().size());
                    PortSingleton.getInstance().getUnUsedList().add(receivePort);
                    System.out.println(PortSingleton.getInstance().getUnUsedList().size());
                    useCodeDataMap.remove(remove);
                    System.out.println(PortSingleton.getInstance().getUnUsedList().size());

                }
            }
            }catch (Exception e) {
              e.printStackTrace();
            }
        }
    }
}

