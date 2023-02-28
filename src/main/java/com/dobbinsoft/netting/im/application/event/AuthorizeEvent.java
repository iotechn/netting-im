package com.dobbinsoft.netting.im.application.event;

import com.dobbinsoft.netting.server.event.IOEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizeEvent implements IOEvent {

    private String userId;

    private String userSecret;

    @Override
    public int eventCode() {
        return IMEventCodes.AUTHORIZE;
    }
}
