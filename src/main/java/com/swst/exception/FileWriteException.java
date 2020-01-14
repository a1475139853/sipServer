package com.swst.exception;

/**
 * 文件写入错误
 *
 * @author yxh
 * @date 2019-11-08 15:34
 */
public class FileWriteException extends RuntimeException {
    public FileWriteException() {
    }

    public FileWriteException(String message) {
        super(message);
    }
}
