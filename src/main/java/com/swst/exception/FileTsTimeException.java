package com.swst.exception;

/**
 * ts 文件格式错误
 *
 * @author yxh
 * @date 2019-12-15 15:36
 */
public class FileTsTimeException extends RuntimeException {
    public FileTsTimeException() {
    }

    public FileTsTimeException(String message) {

        super(message);
    }
}
