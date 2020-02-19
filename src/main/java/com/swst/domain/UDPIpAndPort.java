package com.swst.domain;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.Data;

/**
 * @Auther: fregun
 * @Date: 20-2-15 16:24
 * @Description:
 */
@Data
public class UDPIpAndPort {
    private String ip;
    private int port;
    private String recIp;
    private int recPort;
    private Channel channel;
    private boolean push;

    /**
     * 阀值  (每一秒加一  大于8 秒就默认该连接断开)
     *
     */
    private int  threshold;
    public UDPIpAndPort(String recIp,int recPort,String ip,int port,Channel channel){
        this.recIp = recIp;
        this.recPort =recPort;
        this.ip = ip;
        this.port = port;
        this.channel = channel;
    }
}
