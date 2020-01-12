package com.swst.videoServer;

import com.swst.config.SpringContextHolder;
import com.swst.config.StreamConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class PortSingleton {
    private StreamConfig streamConfig = (StreamConfig)SpringContextHolder.getBean("streamConfig");
    private static volatile PortSingleton ourInstance = null;
    //存储未使用接收数据端口信息
    List<Integer> unUsedList = new ArrayList<>();
    //存储已使用接收端口信息、已使用接收端口绑定信息
    Map<String,String> usedMap = new HashMap<>();//键：　媒体发送者编码/媒体发送者ip+port　值：　接收ip+port

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
     *
     * @param cameraCode 媒体流发送者设备编码
     * @return 流接收 ip+":"+port
     */
    public String getRecIpAndPort(String cameraCode){
        if(unUsedList.size()>0){
            Integer port = unUsedList.get(0);
            Integer remove = unUsedList.remove(0);
            String ip = streamConfig.getIp();
            usedMap.put(cameraCode,ip+":"+port);
            return ip+":"+port;
        }
        return null;
    }
    public void addPort(int port){
        this.unUsedList.add(port);
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
