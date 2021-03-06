package com.swst.domain;

import io.netty.channel.ChannelFuture;
import lombok.Data;

import java.io.Serializable;

@Data
public class IpAndPort implements Serializable {
    private String ip;
    private int port;
    private ChannelFuture sync;
    private boolean push;
}
