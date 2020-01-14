package com.swst.tools;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.swst.exception.FileTsTimeException;
import com.swst.exception.FileWriteException;
import com.swst.exception.M3u8FileNotFoundException;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * M3u8视频处理工具
 *
 * @author yxh
 * @date 2019-11-06 10:13
 */
public class FileM3u8Utils {
    private FileM3u8Utils() {
    }

    /**
     * 加载外部m3u8索引目录
     * 路径示例 /93/93.m3u8
     *
     * @param path 外部索引文件
     * @return java.util.Map 返回索引目录
     * @date 2019/11/6 11:36
     **/
    public static Map<String, Set<String>> loadCaseFile(@NotNull final String path) {
        String suffix = ".m3u8";
        if (!path.endsWith(suffix)) {
            throw new M3u8FileNotFoundException("沒有找到对应的M3u8文件");
        }
        Map<String, Set<String>> map = new HashMap<>(361);
        try (FileReader reader = new FileReader(path);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.endsWith(".ts")) {
                    List<String> list = Splitter.on("/")
                            .trimResults()
                            .omitEmptyStrings()
                            .splitToList(line);
                    if (map.containsKey(list.get(0))) {
                        Set<String> swap = map.get(list.get(0));
                        swap.add(list.get(1));
                        map.put(list.get(0), swap);
                    } else {
                        Set<String> dataTime = new HashSet<>();
                        dataTime.add(list.get(1));
                        map.put(list.get(0), dataTime);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 判断是否包含子文件或子文件夹。
     *
     * @param f 文件
     * @return true表示包含子文件或子文件夹。
     */
    public static boolean isHasChildren(@NotNull final File f) {
        boolean flag = false;
        File[] list = null;
        if (f.isDirectory()) {
            list = f.listFiles();
        }
        if (null != list && list.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 搜索文件或文件夹
     * 遍历文件生成文件树
     * @param folderPath 要搜索的路径。
     * @param fileFilter 接口FileFilter。过滤出符合条件的File。
     * @return 符合搜索条件的File。
     */
    public static List<TreeFile> search(final String folderPath, final FileFilter fileFilter) {
        File folder = new File(folderPath);
        //返回树形状
        ArrayList<TreeFile> result = new ArrayList<TreeFile>();
        //树中当前层文件节点的集合。
        ArrayList<File> currentLevelNodes = new ArrayList<File>();
        currentLevelNodes.add(folder);
        //初始化父节点
        TreeFile parentNode = new TreeFile();
        parentNode.setParentIndex(null);
        parentNode.setIndex(folderPath);
        parentNode.setLevelIndex(null);
        //兄弟节点
        TreeFile levelIndexNode = null;
        getFileArrayList(folder,parentNode,levelIndexNode,result,fileFilter);
        return result;
    }

    private static void getFileArrayList(File file, TreeFile parentNode, TreeFile levelIndexNode, ArrayList<TreeFile> result, FileFilter fileFilter){
        // 下一层文件节点的集合。
        ArrayList<File> nextLevelNodes = new ArrayList<File>();
        //遍历树中当前层文件节点的集合
        if(fileFilter.filter(file)){
            TreeFile treeFileNode = new TreeFile();
            treeFileNode.setParentIndex(parentNode);
            treeFileNode.setIndex(file.getName());
            if (Objects.nonNull(levelIndexNode)) {
                treeFileNode.setLevelIndex(levelIndexNode);
            }
            //当前节点变为父节点
            parentNode = treeFileNode;
//            //当前节点变为兄弟节点
//            levelIndexNode = treeFileNode;
            //子节点
            List<String> childNode = new ArrayList<>();
            // 如果有子文件节点，就把子文件节点加入 nextLevelNodes
            if (isHasChildren(file)) {
                if (file.listFiles() == null) {
                    return ;
                }
                for (File childFile : file.listFiles()) {
                    if (childFile.isDirectory()) {
                        childNode.add(childFile.getName());
                    }
                    nextLevelNodes.add(childFile);
                }
            }
            //设置子节点
            treeFileNode.setChildIndex(childNode);
            result.add(treeFileNode);
        }

        for (int i=0;i<nextLevelNodes.size();i++) {
            if(i==0){
                levelIndexNode=null;
            }
            getFileArrayList(nextLevelNodes.get(i),parentNode,levelIndexNode,result,fileFilter);
        }
    }


    /**
     * m3u8视频表生成
     * <p>
     * Examples:
     * <blockquote><pre>
     * loadM3u8File（“record\1995361582612541212\20191021\20191021175906\1995361582612541212.m3u8”，"record")
     * return "4.137" ,"1995361582612541212\20191021\20191021175906\xx.m3u8"
     * </pre></blockquote>
     *
     * @param path m3u6文件路径
     * @param stop ts目录停止点
     * @return java.util.List<java.util.List < java.lang.String>> 返回二维的视频列表
     * @date 2019/11/8 14:11
     **/
    public static Map<String, List<String>> loadM3u8File(@NotNull final String path, @NotNull final String stop) {
        Map<String, List<String>> map = new HashMap<>(4);
        String suffix = ".m3u8";
        if (!path.endsWith(suffix)) {
            throw new M3u8FileNotFoundException("沒有找到对应的M3u8文件");
        }
        List<String> swap = Splitter.on(File.separator)
                .trimResults()
                .splitToList(path);
        //获取目录名
        //去掉自己
        int tail = 2;
        StringBuilder directory = new StringBuilder();
        for (int i = swap.size() - tail; i >= 0; i--) {
            String dir = swap.get(i);
            if (stop.equals(dir)) {
                directory.delete(0, 1);
                break;
            }
            directory.insert(0, "/" + dir);
        }
        //读取m3u8文件
        try (FileReader reader = new FileReader(path);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            List<String> len = new LinkedList<>();
            List<String> ts = new LinkedList<>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#EXTINF:")) {
                    String time = line.substring(8, line.length() - 1);
                    len.add(time);
                }
                if (line.endsWith(".ts")) {
                    ts.add(directory + "/" + line);
                }
            }
            map.put("time", len);
            map.put("tsName", ts);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 数据合成
     *
     * @param beforeTable 待合成mu38二维的前视频列表
     * @param afterTable  待合成mu38二维的后视频列表
     * @return java.lang.String 返回文件路径
     * @date 2019/11/8 14:41
     **/
    public static String mergeM3u8(Map<String, List<String>> beforeTable, Map<String, List<String>> afterTable, String extendUrl) {
        File temp = null;
        String path = null;
        try {
            File dir = new File(System.getProperty("user.dir") + File.separator + "video");
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    throw new FileWriteException("文件无法创建");
                }
            }
            String id = CharMatcher.anyOf("-")
                    .removeFrom(UUID.randomUUID().toString());
            temp = File.createTempFile(id, ".m3u8", dir);
            FileWriter fileWriter = new FileWriter(temp, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.append("#EXTM3U\n")
                    .append("#EXT-X-VERSION:3\n")
                    .append("#EXT-X-MEDIA-SEQUENCE:0\n")
                    .append("#EXT-X-TARGETDURATION:5\n");
            List<String> beforeVideoLen = beforeTable.get("time");
            double beforeSum = beforeVideoLen.stream()
                    .map(Double::parseDouble)
                    .collect(Collectors.summarizingDouble(value -> value))
                    .getSum();
            List<String> beforeVideoUrl = beforeTable.get("tsName");
            List<String> afterVideoLen = afterTable.get("time");
            double afterSum = afterVideoLen.stream()
                    .map(Double::parseDouble)
                    .collect(Collectors.summarizingDouble(value -> value))
                    .getSum();
            List<String> afterVideoUrl = afterTable.get("tsName");
            double sum = beforeSum + afterSum;
            //保留3位小数
            DecimalFormat df = new DecimalFormat("#.000");
            writer.append("#ZEN-TOTAL-DURATION:")
                    .append(df.format(sum))
                    .append("\n");
            if (beforeVideoLen.size() != beforeVideoUrl.size() || afterVideoLen.size() != afterVideoUrl.size()) {
                throw new FileWriteException("文件列表不匹配");
            }
            accumulator(beforeVideoLen, beforeVideoUrl, writer, extendUrl);
            accumulator(afterVideoLen, afterVideoUrl, writer, extendUrl);
            writer.append("#EXT-X-ENDLIST");
            writer.flush();
            writer.close();
            fileWriter.close();
            path = temp.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 根据 文件路径 生成 ts列表
     *
     * @param path ts文件目录
     * @param stop ts路径截取位
     * @param format 目录时间格式
     * @return 返回文件列表
     */
    public static Map<String, List<String>> loadTsFile(final String path, final String stop, final String format) {
        //加载文件
        File folder = new File(path);
        List<String> TempBefoteTime = Splitter.on("/")
                .trimResults()
                .splitToList(path);
        String timeString=TempBefoteTime.get(TempBefoteTime.size() - 1);
        LocalDateTime time = parseStringToDateTime(timeString,format);
        String[] filename = folder.list();
        List<String> list = Arrays.stream(Objects.requireNonNull(filename))
                .filter(f -> f.endsWith(".ts"))
                .sorted()
                .collect(Collectors.toList());
        //时间列表
        List<String> timeList = new ArrayList<>();
        long beforeTime = getLongTime(list.get(0));
        for (int i = 1,len = list.size();i <len;i++) {
            long nextTime = getLongTime(list.get(i));
            String second = bothTimestamps(beforeTime,nextTime);
            timeList.add(second);
            beforeTime = nextTime;
        }
        timeList.add("2.0");
        Map<String, List<String>> map = new HashMap<>(4);
        //数据
        String temp = path.substring(path.lastIndexOf(stop) + stop.length() + 1);
        List<String> dataList = list.stream().map(f -> (temp + File.separator +f)).collect(Collectors.toList());
        map.put("time", timeList);
        map.put("tsName", dataList);
        return map;
    }

    /**
     * 获取时间戳之间的秒数
     * @param beforeTsTimestamp 前一个时间戳
     * @param afterTsTimestamp 后一个时间搓
     * @return 返回二者时间秒数
     */
    private static String bothTimestamps(long beforeTsTimestamp, long afterTsTimestamp){
        return String.valueOf(Math.abs(afterTsTimestamp - beforeTsTimestamp) / 1000.00);
    }


    /**
     * LocalDateTime转时间戳
     * @param localDateTime 时间格式
     * @return 返回时间戳
     */
    private static long getTimestampOfDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 取ts文件名的时间戳
     *
     * @param tsPath ts文件路径
     * @return 时间戳
     * @date 2019/12/15 16:16
     */
    private static long getLongTime(final String tsPath) {
        List<String> TimeList = Splitter.on('-')
                .splitToList(tsPath)
                .stream()
                .filter(f -> f.endsWith(".ts"))
                .map(f -> f.substring(0, f.lastIndexOf(".ts")))
                .collect(Collectors.toList());
        if (TimeList.size() > 1) {
            throw new FileTsTimeException("ts文件命名重复");
        }
        return Long.valueOf(TimeList.get(0));
    }


    /**
     * 自定义时间转换
     *
     * @param time 时间
     * @param format 时间格式
     * @return 返回localdatatime
     */
    public static LocalDateTime parseStringToDateTime(String time, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }
    /**
     * 累加器，将数据逐行写入
     *
     * @param vlen      视频长度
     * @param vurl      视频地址
     * @param writer    写入流
     * @param extendUrl 地址扩展
     * @date 2019/11/8 16:06
     **/
    private static void accumulator(List<String> vlen, List<String> vurl, BufferedWriter writer, String extendUrl) throws IOException {
        for (int i = 0, len = vlen.size(); i < len; i++) {
            writer.append("#EXTINF:")
                    .append(vlen.get(i))
                    .append(",\n");
            if (extendUrl != null) {
                writer.append(extendUrl)
                        .append(vurl.get(i))
                        .append("\n");
            } else {
                writer.append(vurl.get(i))
                        .append("\n");
            }
        }
    }


}
