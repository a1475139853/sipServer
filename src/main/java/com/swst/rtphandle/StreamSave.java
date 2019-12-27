package com.swst.rtphandle;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

import java.io.*;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @Auther: fregun
 * @Date: 19-12-6 15:23
 * @Description: Stream 流存储
 */
public class StreamSave {
    public static  int  TIME = 1500;//表示每个文件存储帧数
    static File file = new File("/root/Desktop/test.h264");
    public static FileOutputStream fileOutputStream;
    public static InputStream inputStream;
    public static ServerSocket serverSocket ;
    static {
        try {
            fileOutputStream = new FileOutputStream(file);
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
         catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void handleStream(byte[]bytes,boolean last) throws IOException {
        System.out.println(TIME);
        if(TIME == 0){
            TIME=20;
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

        if(last){
            TIME--;//单一文件夹存储帧数减一
        }
    }
    public static void push(FFmpegBuilder fFmpegBuilder) throws URISyntaxException {
        fFmpegBuilder.addInput("/root/Desktop/test.h264");
    }

}
