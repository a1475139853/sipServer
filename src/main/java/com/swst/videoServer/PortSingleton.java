package com.swst.videoServer;

import java.util.HashMap;
import java.util.Map;

public class PortSingleton {
    private static volatile PortSingleton ourInstance = null;
    Map<Map<Integer, String>,Map<Integer, String>> usedStreamReceiveMap = new HashMap<>(); // 保存已用端口
    Map<Integer, String> unUseMap = new HashMap<>();  // 保存可用端口
    Map<Map<Integer, String>,Map<Integer, String>> usedStreamSendMap = new HashMap<>(); // 保存已用端口


    public static PortSingleton getInstance() {
        if(ourInstance == null){
            synchronized (PortSingleton.class){
                if(ourInstance == null){
                    ourInstance = new PortSingleton();
                }
            }
        }
        return ourInstance;
    }
    private PortSingleton() {

    }

    /**
     * 从可用端口中取一个出来作为某摄像头的流接收存储地址
     * 删除刚分配出去的端口
     * 添加刚分配的端口到已用端口
     * @return
     */
    public String getUnUseMapForStreamReceive(int port,String ip){
        Map<Integer,String> sendMap = null;
        Map<Integer,String> receiveMap = null;
        for (Map.Entry<Integer,String> map:this.unUseMap.entrySet()) {
            receiveMap = new HashMap<>();
            sendMap = new HashMap<>();
            receiveMap.put(map.getKey(),map.getValue());
            sendMap.put(port,ip);
            this.usedStreamReceiveMap.put(sendMap,receiveMap);
            this.unUseMap.remove(map.getKey());
            return map.getKey()+","+map.getValue();
        }
        return "";
    }
    /**
     * 从可用端口中取一个出来作为流发送端口
     * 删除刚分配出去的端口
     * 添加刚分配的端口到已用端口
     * @return
     */
    public String getUnUseMapForStreamSend(int port,String ip){
        Map<Integer,String> sendMap = null;
        Map<Integer,String> receiveMap = null;
        for (Map.Entry<Integer,String> map:this.unUseMap.entrySet()) {
            sendMap = new HashMap<>();
            receiveMap = new HashMap<>();
            sendMap.put(map.getKey(),map.getValue());
            receiveMap.put(port,ip);
            this.usedStreamSendMap.put(sendMap,receiveMap);
            this.unUseMap.remove(map.getKey());
            return map.getKey()+","+map.getValue();
        }
        return "";
    }
}
