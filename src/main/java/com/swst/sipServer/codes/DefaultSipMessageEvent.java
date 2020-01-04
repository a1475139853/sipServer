package com.swst.sipServer.codes;

import gov.nist.javax.sip.message.SIPMessage;

/**
 * @author cormye tcq9121@163.com
 * @Description todo
 * @sine 2019/11/13
 */
public class DefaultSipMessageEvent implements SipMessageEvent {

    private final Connection connection;

    private final SIPMessage msg;

    public DefaultSipMessageEvent(final Connection connection, final SIPMessage msg) {
        this.connection = connection;
        this.msg = msg;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public SIPMessage getMessage() {
        return this.msg;
    }
}
