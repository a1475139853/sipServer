package com.swst.service.video.impl;

import com.swst.service.local.LocalFile;
import com.swst.service.remote.RemoteFile;
import com.swst.service.video.FileServer;
import com.swst.tools.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yxh
 * @date 2019-11-06 9:10
 */
@Service("fileServer")
@Slf4j
public class FileServerImpl implements FileServer {

    @Resource
    private LocalFile localFile;
    @Resource
    private RemoteFile remoteFile;
    @Value("${video.url.path}")
    private String path;


    @Override
    public List<TreeFile> findFileList() {
        String swapPath = verify(path);
        //获取文件树
        List<TreeFile> treefile = localFile.loadFile(swapPath);
        if (CollectionUtils.isEmpty(treefile)) {
            treefile = remoteFile.loadFile();
        }
        if (CollectionUtils.isEmpty(treefile)) {
            log.warn("当前目录不存在文件，调用为空，路径:{}", swapPath);
            throw new NullPointerException("当前文件下没有文件");
        }
        return treefile;
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
