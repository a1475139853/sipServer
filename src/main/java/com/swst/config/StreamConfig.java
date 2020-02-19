package com.swst.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: fregun
 * @Date: 20-1-10 13:56
 * @Description:
 */
@Component
@ConfigurationProperties(prefix = "stream-server")
@Data
public class StreamConfig {
    private String ip;
    private int port;
    private String code;
    private int recPortStart;
    private int recPortEnd;
    private int outPortStart;
    private int outPortEnd;
}
