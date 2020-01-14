package com.swst.service.remote.impl;

import com.swst.service.remote.RemoteFile;
import com.swst.tools.TreeFile;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 加载远程文件
 *
 * @author yxh
 * @date 2019-11-06 9:11
 */
@Service("remoteFile")
public class RemoteFileImpl implements RemoteFile {
    @Override
    public List<TreeFile> loadFile() {
        return null;
    }
}
