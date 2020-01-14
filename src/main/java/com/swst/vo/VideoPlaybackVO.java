package com.swst.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 回放对象
 *
 * @author yxh
 * @date 2019-11-08 16:38
 */
@Data
@Builder
public class VideoPlaybackVO implements Serializable {
    @ApiModelProperty(value="设备编号",name="id",example="1995361582612541212",dataType = "String",notes = "长度为19位")
    private String id;
    @ApiModelProperty(value="播放地址",name="url",example="0f0e261c5d42d47dba1a605a021616201245442424946",dataType = "String",
            notes = "需要自己组装，格式为:http://192.168.6.187:8080/video/download/0f0e261c5d42d47dba1a605a021616201245442424946/1.0")
    private String url;
    @ApiModelProperty(value="跳转时间",name="jumpTime",example="0",dataType = "long",notes = "单位为秒")
    private Long jumpTime;
}
