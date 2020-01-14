package com.swst.tools;

import java.io.File;

/**
 * 文件过滤的接口
 *
 * @author yxh
 * @date 2019-12-07 14:20
 */
public interface FileFilter {

    /**
     * SearchFileUtils.search(final String folderPath, final FileFilter fileFilter) 方法
     * 的回调函数。返回true表示符合过滤条件，会加入到返回结果中。false表示不符合过滤条件，不会在返回结果中。
     *
     * @param file 要被过滤的文件。
     * @return 返回true表示符合过滤条件，会加入到返回结果中。false表示不符合过滤条件，不会在返回结果中。
     */
    boolean filter(File file);
}
