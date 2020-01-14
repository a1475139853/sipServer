package com.swst.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author yxh
 * @date 2019-11-06 15:17
 */
@Data
@Builder
public class VideoVO implements Serializable {
    @ApiModelProperty(value = "设备编号", name = "id", example = "1995361582612541212", dataType = "String", notes = "长度为19位")
    private String id;
    @ApiModelProperty(value = "开始时间地址", name = "startIndex", example = "20191012142236", dataType = "String",
            notes = "开始播放地址，格式为:http://192.168.6.187:8099/record/20191012/20191012142236/1995361582612541212.m3u8")
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime startIndex;
    @ApiModelProperty(value = "下一段时间地址", name = "nextIndex", example = "20191012143236", dataType = "String",
            notes = "下一段时间地址，格式为:http://192.168.6.187:8099/record/20191012/20191012143236/1995361582612541212.m3u8")
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    private LocalDateTime nextIndex;
    @ApiModelProperty(value = "跳转时间", name = "jumpTime", example = "0", dataType = "long", notes = "单位为秒")
    private Long jumpTime;
}
