package com.swst.videoServer;

import java.util.HashMap;
import java.util.Map;

public class PortSingleton {
    private static volatile PortSingleton ourInstance = null;
    public Map<Map<Integer, String>,Map<Integer, String>> usedStreamReceiveBindMap = new HashMap<>(); // 保存接收流已用port+ip和媒体流发送者发送流port+ip关系
    public Map<String,Map<Integer,String>> usedStreamReceiveMap = new HashMap<>();//绑定媒体发送者设备与流接收端口ｉｐ信息
    public Map<Integer, String> unUseMap = new HashMap<>();  // 保存可用端口
    public Map<Map<Integer, String>,Map<Integer, String>> usedStreamSendMap = new HashMap<>(); // 保存发流已用port+ip 和媒体接受者ip+port关系


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
     * 将媒体发送者端口ｉｐ和流媒体服务器接收端口ｉｐ
     * @return
     */
    public void getUnUseMapForStreamReceiveBind(int port,String ip,Map<Integer,String>receiveMap){
        Map<Integer,String> sendMap = null;
            sendMap = new HashMap<>();
            sendMap.put(port,ip);
            this.usedStreamReceiveBindMap.put(sendMap,receiveMap);
    }
    /**
     * 将媒体发送者设备和接收流端口绑定
     * @return
     */
    public String getUnUseMapForStreamReceive(String cameraCode){
        Map<Integer,String> receiveMap = null;
        for (Map.Entry<Integer,String> map:this.unUseMap.entrySet()) {
            receiveMap = new HashMap<>();
            receiveMap.put(map.getKey(),map.getValue());
            this.usedStreamReceiveMap.put(cameraCode,receiveMap);
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
