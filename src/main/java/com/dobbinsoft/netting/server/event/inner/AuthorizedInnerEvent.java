package com.dobbinsoft.netting.server.event.inner;

import com.dobbinsoft.netting.server.event.IOEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizedInnerEvent implements IOEvent{

    private String jwtToken;

    @Override
    public int eventCode() {
        return IOEvent.INNER_EVENT_AUTHORIZED;
    }


}
