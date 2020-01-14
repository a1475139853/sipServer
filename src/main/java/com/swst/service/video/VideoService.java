package com.swst.service.video;

import com.swst.domain.HistoryMessage;
import com.swst.vo.VideoVO;

import java.time.LocalDateTime;

public interface VideoService {

    void send(HistoryMessage historyMessage);

    VideoVO getVideoVO(String id, LocalDateTime time);
}
