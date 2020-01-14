package com.swst.service.video;


import com.swst.tools.TreeFile;

import java.util.List;

/**
 * @author yxh
 * @date 2019-11-05 3:10
 */
public interface FileServer {
    /**
     * 查找m3u8视频目录 获取文件目录list
     *
     * @return java.util.List<?>
     * @date 2019/11/5 3:25
     **/
    List<TreeFile> findFileList();
}
