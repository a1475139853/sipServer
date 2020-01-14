package com.swst.service.local;

import com.swst.tools.TreeFile;

import java.util.List;

/**
 * @author yxh
 * @date 2019-11-05 3:30
 */
public interface LocalFile {
    /**
     * 本地视频目录加载
     *
     * @param path 视频加载路径
     * @return List<TreeFile> 文件索引对象
     * @date 2019/11/6 14:44
     **/
    List<TreeFile> loadFile(String path);

}
