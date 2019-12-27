package com.swst.rtphandle;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Auther: fregun
 * @Date: 19-12-6 15:23
 * @Description: Stream 流存储
 */
public class StreamSave {
    public static int TIME = 1500;//表示每个文件存储帧数
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    static File myPath;
    static {
        myPath = new File(System.getProperty("user.home")  + File.separator + sdf.format(new Date()));
        if (!myPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
            myPath.mkdir();
            System.out.println("创建文件夹路径为：" + myPath);
        }
    }
    public static FileOutputStream fileOutputStream;
    public static InputStream inputStream;
    static File file;

    public static void handleStream(byte[] bytes, boolean last) throws IOException {
        myPath = new File(System.getProperty("user.home")+ File.separator + sdf.format(new Date()));
        if (!myPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
            myPath.mkdir();
            System.out.println("创建文件夹路径为：" + myPath);
        }
        if(file == null){
            file = new File(myPath.getPath() + File.separator + new Date().getTime() + ".h264");
            System.out.println("创建文件路径为：" + file.getPath());
        }

        if(!file.exists())
            file.createNewFile();
        if (TIME == 0) {
            TIME = 1500;
            file = new File(myPath.getPath() + File.separator  + new Date().getTime() + ".h264");
            file.createNewFile();
        }
        try {
            fileOutputStream = new FileOutputStream(file);
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * 调用ffmpegAPI
         */
        fileOutputStream.write(bytes);
        fileOutputStream.flush();
//        FFmpegBuilder fFmpegBuilder = null;
//        push(fFmpegBuilder);
//        if(fFmpegBuilder == null){
//            fFmpegBuilder = new FFmpegBuilder();
//        }

        if (last) {
            TIME--;//单一文件夹存储帧数减一
        }
    }

    public static void push(FFmpegBuilder fFmpegBuilder) throws URISyntaxException {
        fFmpegBuilder.addInput("/root/Desktop/test.h264");
    }

}
