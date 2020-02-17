package com.swst.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: fregun
 * @Date: 20-1-9 11:08
 * @Description:
 */
@Component
@ConfigurationProperties(prefix = "stream-rec")
public class StreamRecConfig {
    private String host;
    private int port;
    private String code;
    private int tcpPort;
    private int tcpHistoryPort;

    public void setTcpHistoryPort(int tcpHistoryPort) {
        this.tcpHistoryPort = tcpHistoryPort;
    }

    public int getTcpHistoryPort() {
        return tcpHistoryPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getCode() {
        return code;
    }
}
