package com.swst.videoServer;

import com.sun.javafx.collections.MappingChange;

import java.util.HashMap;
import java.util.Map;

public class PortSingleton {
    private static volatile PortSingleton ourInstance = null;
    Map<String,Map<Integer, String>> usedmap = new HashMap<>(); // 保存已用端口
    Map<Integer, String> unUseMap = new HashMap<>();  // 保存可用端口

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
     * 从可用端口中取一个出来作为某摄像头的流传输存储地址
     * 删除刚分配出去的端口
     * 添加刚分配的端口到已用端口
     * @return
     */
    public String getUnUseMap(){
        Map<Integer,String> map1 = null;
        for (Map.Entry<Integer,String> map:this.unUseMap.entrySet()) {
            map1 = new HashMap<>();
            map1.put(map.getKey(),map.getValue());
            this.usedmap.put("",map1);
            this.unUseMap.remove(map.getKey());
            return map.getKey()+","+map.getValue();
        }
        return "";
    }
}
