package com.swst.utils;

import java.util.Date;

/**
 * @Auther: fregun
 * @Date: 19-12-28 16:47
 * @Description:
 */
public class Generate {
    public static String generateTag() {
        return "tag=" + String.valueOf(System.currentTimeMillis()).substring(3);
    }
    public static String getTag(){
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }

}