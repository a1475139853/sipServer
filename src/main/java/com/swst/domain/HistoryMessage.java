package com.swst.domain;

import com.swst.vo.VideoVO;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.Data;

/**
 * @Auther: fregun
 * @Date: 20-1-13 10:54
 * @Description:
 */
@Data
public class HistoryMessage {
    private UDPIpAndPort udpIpAndPort;
    private SIPResponse sipResponse;
    private VideoVO videoVO;
    private boolean b;
}
