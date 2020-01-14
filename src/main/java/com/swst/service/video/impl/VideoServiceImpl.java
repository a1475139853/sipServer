package com.swst.service.video.impl;

import com.swst.domain.HistoryMessage;
import com.swst.service.video.FileServer;
import com.swst.service.video.VideoService;
import com.swst.tools.FileM3u8Utils;
import com.swst.tools.TreeFile;
import com.swst.utils.FileUploadFile;
import com.swst.vo.VideoVO;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {
    @Value("${video.url.path}")
    private String path;

    @Value("${video.play.rollbackTime}")
    private String length;

    @Resource
    private FileServer fileServer;
    private int byteRead;
    private volatile int start = 0;
    private volatile int lastLength = 0;
    public RandomAccessFile randomAccessFile;

    @Override
    public void send(HistoryMessage historyMessage) {
        //获取VideoVO
        VideoVO vo = historyMessage.getVideoVO();
        //判断vo是否为空（为空则说明没有该视频）
        if(vo==null){
            return ;
        }
        //vo不为空 构建出目录
        String bymd = vo.getStartIndex().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String byMdHms = vo.getStartIndex().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        path = verify(path);
        //文件目录url
        String burl= path + vo.getId() + File.separator + bymd + File.separator + byMdHms;
        //netty发送文件
        out(burl,vo.getId(),historyMessage.getIpAndPort().getSync());

        //判断是否要发下个文件夹的数据
        LocalDateTime nextIndex = vo.getNextIndex();
        if(nextIndex!=null){
            String aymd = nextIndex.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String ayMdHms = nextIndex.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            //下个视频文件夹url
            String aurl = path + vo.getId() + File.separator + aymd + File.separator + ayMdHms;
            //netty发送文件
            out(aurl,vo.getId(),historyMessage.getIpAndPort().getSync());
        }
    }

    /**
     * netty发送文件
     * @param url 目录路径
     * @param id 摄像头id
     */
    private void out(String url, String id, ChannelFuture channelFuture){
        File file = new File(url);
        //如果有子文件
        if(FileM3u8Utils.isHasChildren(file)){
            //获取子文件集合
            File[] files = file.listFiles();
            //获取文件名中时间戳集合
            List<Long> timeList = new ArrayList();
            for (File f : files){
                timeList.add(Long.valueOf(f.getName().substring(f.getName().indexOf("-")+1, f.getName().lastIndexOf("."))));
            }
            //时间戳排序
            Collections.sort(timeList);
            //发送
            for (Long timeLong : timeList){
                File f = new File(url + File.separator + id + "-" + timeLong + ".h264");
                FileUploadFile fileUploadFile = new FileUploadFile();
                String fileMd5 = f.getName();
                fileUploadFile.setFile(f);
                fileUploadFile.setFile_md5(fileMd5);
                try {
                        randomAccessFile = new RandomAccessFile(f,
                                "r");
                        randomAccessFile.seek(0);
//			lastLength = 1024 * 10;
                        long length = f.length();
                        lastLength = (int)length;
                        byte[] bytes = new byte[lastLength];
                        if ((byteRead = randomAccessFile.read(bytes)) != -1) {
                            fileUploadFile.setEndPos(byteRead);
                            fileUploadFile.setBytes(bytes);
                            channelFuture.channel().writeAndFlush(fileUploadFile);
                            System.out.println(timeLong);
                        } else {
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public VideoVO getVideoVO(String id, LocalDateTime time) {
        //遍历文件夹 获取文件以及目录组成的List
        List<TreeFile> tree = fileServer.findFileList();

        //获取默认值
        long defaultValue = defaultValue();
        String ymd = time.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        //时间格式化
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        //初始化日期目录
        List<String> tempDateList = null;

        //判断设备是否存在 即是否存在该摄像头
        for (TreeFile file: tree) {
            String fileName = file.getIndex();
            //如果存在与摄像头id相同的目录 说明设备存在
            if(fileName.equals(id)){
                //获取该设备下的date子目录
                tempDateList = file.getChildIndex();
                //遍历到了摄像头id目录，跳出循环
                break;
            }
        }
        //设备不存在 抛出异常
        if (CollectionUtils.isEmpty(tempDateList)) {
            throw new NullPointerException("不存在此编号" + id);
        }
        //设备存在 判定有那天数据有没有
        String findData = null;
        findData = tempDateList.stream().filter(x -> x.equals(ymd)).collect(Collectors.toList()).get(0);
        if (StringUtils.isEmpty(findData)) {
            throw new NullPointerException("不存在此" + ymd + "目录");
        }
        //取对应的时间点
        for (TreeFile x : tree) {
            String index = x.getIndex();
            if (index.equals(findData) && x.getParentIndex().getIndex().equals(id)) {
                List<String> list = x.getChildIndex();
                Collections.sort(list);
                LocalDateTime endTime = LocalDateTime.parse(list.get(list.size() - 1), df);
                if (time.isAfter(endTime)) {
                    long jumpTime = ChronoUnit.SECONDS.between(endTime, time);
                    long needjump = jumpTime - Long.valueOf(length);
                    if (needjump > 0) {
                        return VideoVO.builder()
                                .id(id)
                                .startIndex(endTime)
                                .nextIndex(null)
                                .jumpTime(jumpTime - Long.valueOf(length))
                                .build();
                    } else {
                        LocalDateTime beforeTime = LocalDateTime.parse(list.get(list.size() - 2), df);
                        long beforjumplen = ChronoUnit.SECONDS.between(beforeTime, endTime) + needjump;
                        return VideoVO.builder()
                                .id(id)
                                .startIndex(beforeTime)
                                .nextIndex(endTime)
                                .jumpTime(beforjumplen)
                                .build();
                    }
                }
                for (int i = 0, len = list.size(); i < len; i++) {
                    //start time
                    LocalDateTime startTime = LocalDateTime.parse(list.get(i), df);
                    //下个时间记录
                    LocalDateTime afterTime = getAfterTime(i, len, list, df);
                    //刚好等于开始
                    if (time.isEqual(startTime)) {
                        return VideoVO.builder()
                                .id(id)
                                .startIndex(startTime)
                                .nextIndex(afterTime)
                                .jumpTime(0L)
                                .build();
                    }
                    //刚好等于结束
                    if (time.isEqual(afterTime)) {
                        return VideoVO.builder()
                                .id(id)
                                .startIndex(afterTime)
                                .nextIndex(null)
                                .jumpTime(0L)
                                .build();
                    }
                    //在列表之间的
                    if (time.isAfter(startTime) && time.isBefore(afterTime)) {
                        //查找上个时间记录
                        LocalDateTime beforeTime = getbeforeTime(i, len, list, df);
                        //上个视频时长
                        long duration = ChronoUnit.SECONDS.between(beforeTime, startTime);
                        //获取跳跃的时间秒数
                        long jumpTime = ChronoUnit.SECONDS.between(startTime, time);
                        if (jumpTime >= defaultValue) {
                            return VideoVO.builder()
                                    .id(id)
                                    .startIndex(startTime)
                                    .nextIndex(afterTime)
                                    .jumpTime(jumpTime - defaultValue)
                                    .build();
                        } else {
                            jumpTime = duration + jumpTime - defaultValue;
                            jumpTime = jumpTime < 0 ? 0 : jumpTime;
                            if (beforeTime.isEqual(startTime)) {
                                return VideoVO.builder()
                                        .id(id)
                                        .startIndex(startTime)
                                        .nextIndex(afterTime)
                                        .jumpTime(0L)
                                        .build();
                            }
                            return VideoVO.builder()
                                    .id(id)
                                    .startIndex(beforeTime)
                                    .nextIndex(startTime)
                                    .jumpTime(jumpTime)
                                    .build();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取默认值
     *
     * @return long
     * @date 2019/11/7 13:45
     **/
    private long defaultValue() {
        if (StringUtils.isEmpty(length)) {
            return 10;
        }
        return Long.parseLong(length);
    }

    /**
     * 返回数组的当前下个记录点
     *
     * @param now  当前记录点
     * @param len  最大长度
     * @param list 检查的数组对象
     * @param df   时间格式化
     * @return java.time.LocalDateTime 返回下个记录时间
     * @date 2019/11/8 16:52
     **/
    private LocalDateTime getAfterTime(int now, int len, List<String> list, DateTimeFormatter df) {
        LocalDateTime afterTime = null;
        if (now + 1 == len) {
            afterTime = LocalDateTime.parse(list.get(now), df);
        } else {
            afterTime = LocalDateTime.parse(list.get(now + 1), df);
        }
        return afterTime;
    }

    /**
     * 返回数组的当前上个记录点
     *
     * @param now  当前记录点
     * @param len  最大长度
     * @param list 检查的数组对象
     * @param df   时间格式化
     * @return java.time.LocalDateTime 返回上个记录时间
     * @date 2019/11/8 16:52
     **/
    private LocalDateTime getbeforeTime(int now, int len, List<String> list, DateTimeFormatter df) {
        LocalDateTime beforeTime = null;
        if (now >= 1) {
            beforeTime = LocalDateTime.parse(list.get(now - 1), df);
        } else {
            beforeTime = LocalDateTime.parse(list.get(0), df);
        }
        return beforeTime;
    }

    /**
     * 校验路径为空
     *
     * @param path 加载路径
     * @return java.lang.String
     * @date 2019/11/6 9:27
     **/
    private String verify(String path) {
        if (StringUtils.isEmpty(path)) {
            return System.getProperty("user.dir") + "video";
        }
        return path;
    }
}
