package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;

/**
 * 消息包装类
 */
public interface SipMessageEvent {

    Connection getConnection();

    SIPMessage getMessage();

}
