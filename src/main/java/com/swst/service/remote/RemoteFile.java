package com.swst.service.remote;


import com.swst.tools.TreeFile;

import java.util.List;

/**
 * @author yxh
 * @date 2019-11-05 16:47
 */
public interface RemoteFile {
    /**
     * 加载远程文件
     *
     * @return java.util.List<?>
     * @date 2019/11/6 9:14
     **/
    List<TreeFile> loadFile();
}
