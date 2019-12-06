package com.swst.rtphandle;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Auther: fregun
 * @Date: 19-12-6 15:23
 * @Description: Stream 流存储
 */
public class StreamSave {
    public static  int  TIME = 2500;//表示每个文件存储帧数
    static File file = new File("/root/Desktop/test.h264");
    public static FileOutputStream fileOutputStream;

    static {
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void handleStream(byte[]bytes,boolean last) throws IOException {
        System.out.println(TIME);
        if(TIME == 0){
            System.out.println(TIME);
        }
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
        if(last){
            TIME--;//单一文件夹存储帧数减一
        }
    }

}
