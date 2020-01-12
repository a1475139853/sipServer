package com.swst.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: fregun
 * @Date: 20-1-10 13:56
 * @Description:
 */
@Component
@ConfigurationProperties(prefix = "stream-server")
public class StreamConfig {
    private String ip;
    private int port;
    private String code;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
