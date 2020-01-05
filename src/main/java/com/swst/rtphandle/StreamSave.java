package com.swst.rtphandle;

import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @Auther: fregun
 * @Date: 19-12-6 15:23
 * @Description: Stream 流存储
 */
public class StreamSave {
    int TIME = 150;//表示每个文件存储帧数
    SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
    Date time = new Date();
    File firstPath;
    File secondPath;
    File thirdPath;
    String ip;
    int port;

    {
        if (ip != null) {
            time = new Date();
            firstPath = new File(System.getProperty("user.home") + File.separator + ip);
            if (!firstPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                firstPath.mkdir();
                System.out.println("创建文件夹路径为：" + firstPath);
            }
            secondPath = new File(firstPath + File.separator + yyyyMMdd.format(time));
            if (!secondPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                secondPath.mkdir();
                System.out.println("创建文件夹路径为：" + secondPath);
            }
            thirdPath = new File(secondPath + File.separator + yyyyMMddHHmmss.format(time));
            if (!thirdPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                thirdPath.mkdir();
                System.out.println("创建文件夹路径为：" + thirdPath);
            }
        }

    }

    FileOutputStream fileOutputStream;
    InputStream inputStream;
    File file;

    public void handleStream(byte[] bytes, boolean last, String ip) throws IOException {
        if (ip != null) {
            firstPath = new File(System.getProperty("user.home") + File.separator + ip);
            if (!firstPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                firstPath.mkdir();
                System.out.println("创建文件夹路径为：" + firstPath);
            }
            secondPath = new File(firstPath + File.separator + yyyyMMdd.format(time));
            if (!secondPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                secondPath.mkdir();
                System.out.println("创建文件夹路径为：" + secondPath);
            }
            thirdPath = new File(secondPath + File.separator + yyyyMMddHHmmss.format(time));
            if (!thirdPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                thirdPath.mkdir();
                System.out.println("创建文件夹路径为：" + thirdPath);
            }
            if (last && new Date().getTime() - time.getTime() > 180000L) {
                time = new Date();
                thirdPath = new File(secondPath + File.separator + yyyyMMddHHmmss.format(time));
                if (!thirdPath.exists()) {//若此目录不存在，则创建之// 这个东西只能简历一级文件夹，两级是无法建立的。。。。。
                    thirdPath.mkdir();
                    System.out.println("创建文件夹路径为：" + thirdPath);
                }
            }

            if (file == null) {
                file = new File(thirdPath.getPath() + File.separator + new Date().getTime() + ".h264");
                file.createNewFile();
                System.out.println("创建文件路径为：" + file.getPath());
                try {
                    fileOutputStream = new FileOutputStream(file);
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }

            if (!file.exists())
                file.createNewFile();
            if (TIME == 0) {
                TIME = 1500;
                file = new File(thirdPath.getPath() + File.separator + new Date().getTime() + ".h264");
                file.createNewFile();
                try {
                    fileOutputStream = new FileOutputStream(file);
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
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
