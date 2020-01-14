package com.swst.exception;

/**
 * 文件
 *
 * @author yxh
 * @date 2019-11-06 11:49
 */
public class M3u8FileNotFoundException extends RuntimeException {
    public M3u8FileNotFoundException() {
    }

    public M3u8FileNotFoundException(String message) {
        super(message);
    }
}
