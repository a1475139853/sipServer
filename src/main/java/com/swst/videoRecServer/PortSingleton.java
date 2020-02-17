package com.swst.videoRecServer;

import com.swst.config.SpringContextHolder;
import com.swst.config.StreamConfig;
import com.swst.domain.DataInfo;
import io.netty.channel.Channel;

import java.util.*;

public class PortSingleton {
    private StreamConfig streamConfig = (StreamConfig)SpringContextHolder.getBean("streamConfig");
    private static volatile PortSingleton ourInstance = null;
//    @Autowired(required = false)
//    private  StreamConfig streamConfig;
    //存储未使用接收数据端口信息
     private List<Integer> unUsedList = new ArrayList<>();
     //存储未使用发送数据端口信息
     private Map<Integer, Channel> unUsedOutMap = new HashMap<>();

    //存储已使用接收端口信息、已使用接收端口绑定信息
    Map<String,String> usedMap = new HashMap<>();//键：　媒体发送者编码/媒体发送者ip+port　值：　接收ip+port


    //  以code 为键  存储已经使用的 接收端 ip 端口  摄像头 ip
    private  Map<String , DataInfo> useCodeDataMap=new HashMap<>();

    // 以 摄像头ip+端口作为值 存储已经使用的 接收端 ip 端口  摄像头 ip
    private  Map<String ,DataInfo> useIpPortDataMap=new HashMap<>();

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
            for (int i:unUsedList){
                System.out.print(i+"  ");
            }
            System.out.println();
            Integer port = unUsedList.get(0);
            Integer remove = unUsedList.remove(0);
            String ip = streamConfig.getIp();
            usedMap.put(cameraCode,ip+":"+port);
           // 接收摄像头流 的接收端 的code

            String localcode = streamConfig.getCode();
            DataInfo dataInfo = new DataInfo();
            dataInfo.setReceiveCode(localcode);
            dataInfo.setCameraCode(cameraCode);
             dataInfo.setReceivePort(port);
             dataInfo.setReceiveIp(ip);
             useCodeDataMap.put(cameraCode,dataInfo);

            return ip+":"+port;
        }
        return null;
    }
    public void addPort(int port){
        this.unUsedList.add(port);
    }
    public void addOutPort(int port,Channel channel){
        this.unUsedOutMap.put(port,channel);
    }
    public void removeOutPort(int port){
        this.unUsedOutMap.remove(port);
    }
    //获取端口
    public Integer getOutPort(){
        Set<Integer> integers = unUsedOutMap.keySet();
        Iterator<Integer> iterator = integers.iterator();
        if(iterator.hasNext()){
            return iterator.next();
        }
        return null;
    }
    //获取Ｃｈａｎｎｅｌ
    public Channel getOutChanner(int port){
        return unUsedOutMap.get(port);
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

    public Map<String, DataInfo> getUseCodeDataMap() {
        return useCodeDataMap;
    }

    public void setUseCodeDataMap(Map<String, DataInfo> useCodeDataMap) {
        this.useCodeDataMap = useCodeDataMap;
    }

    public Map<String, DataInfo> getUseIpPortDataMap() {
        return useIpPortDataMap;
    }

    public void setUseIpPortDataMap(Map<String, DataInfo> useIpPortDataMap) {
        this.useIpPortDataMap = useIpPortDataMap;
    }

    public List<Integer> getUnUsedList() {
        return unUsedList;
    }

    public void setUnUsedList(List<Integer> unUsedList) {
        this.unUsedList = unUsedList;
    }
}
