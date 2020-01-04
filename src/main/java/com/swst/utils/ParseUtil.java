package com.swst.utils;

/**
 * @Auther: fregun
 * @Date: 19-11-20 15:08
 * @Description:
 */
public class ParseUtil {

     //g根据from信息获取获取设备编号
    public static String parseMessage(String message){
         if(message == null){
             throw  new NullPointerException();
         }
      return message.substring(message.indexOf(":") + 1, message.indexOf("@"));
    }

}
