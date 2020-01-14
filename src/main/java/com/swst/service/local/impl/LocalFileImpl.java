package com.swst.service.local.impl;

import com.swst.service.local.LocalFile;
import com.swst.tools.FileM3u8Utils;
import com.swst.tools.TreeFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FilenameFilter;
import java.util.List;

/**
 * 加载本地文件
 *
 * @author yxh
 * @date 2019-11-05 17:10
 */
@Service("localFile")
@Slf4j
public class LocalFileImpl implements LocalFile {
    @Override
    public List<TreeFile> loadFile(String path) {
         final String yyyyddmmhhMMss = "^(((20[0-3][0-9](0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|(20[0-3][0-9](0[2469]|11)(0[1-9]|[12][0-9]|30)))(20|21|22|23|[0-1][0-9])[0-5][0-9][0-5][0-9])$";
         final String yyyyddmm = "\\b\\d{4}((?:0[13578]|1[02])(?:0[1-9]|[12]\\d|3[01])|02(?:0[1-9]|[12]\\d)|(?:0[469]|11)(?:0[1-9]|[12]\\d|30))\\b";
         List<TreeFile> list = FileM3u8Utils.search(path, (file)->{
//            String tmpName = file.getName();
//            if(tmpName.equals("record")){
//                return true;
//            }
//            if(tmpName.length() == 19){
//                return true;
//            }
//            if(tmpName.matches(yyyyddmm)){
//                return Integer.valueOf(tmpName) > 19000101;
//            }
//            if(tmpName.matches(yyyyddmmhhMMss)){//20191204175903
//                return (new BigInteger("19000101000000").compareTo(new BigInteger(tmpName)) == -1);
//            }
            return true;
        });
        return list;
    }



    /**
     * 过滤文件
     *
     * @return java.io.FilenameFilter
     * @date 2019/11/6 13:15
     **/
    private FilenameFilter filterLocal() {
        return (dir, name) -> {
            String ts = ".ts";
            String m3u8 = ".m3u8";
            return !name.endsWith(ts) && !name.endsWith(m3u8);
        };
    }
}
