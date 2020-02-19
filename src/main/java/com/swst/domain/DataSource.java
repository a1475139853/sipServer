package com.swst.domain;

import lombok.Data;

/**
 * @author frosty
 * @description  新版本  保存 数据源  中间 目的数据 的元数据
 * @date 2020-02-17 17:11:03
 */

@Data
public class DataSource {

    /**
     * 数据唯一标识   uuid
     */
    private String id;

    /**
     * 视频源code
     */
    private String sourceCode;
    /**
     * 视频源 ip
     */
    private String sourceIp;

    /**
     * 视频源端口
     */
    private Integer sourcePort;


    /**
     * 本地(中转)code
     */

    private String localCode;

    /**
     * 本地 ip(中转)
     */
    private String localIp;

    /**
     * 本地接收视频端口
     */
    private Integer LocalRecPort;

    /**
     * 本地发送视频端口
     */
    private Integer LocalSendPort;


    /**
     * 阀值   中间端 接收端 与发送端 分开进行  一个阀值就行
     */
    private int threshold;


    /**
     * 目标code(接收本地发的视频)
     */
    private String targetCode;

    /**
     * 目标port
     */
    private Integer targetPort;

    private String targetIp;


}
