package com.dobbinsoft.netting.im.application.event.event;

import com.dobbinsoft.netting.im.application.event.EventCause;
import com.dobbinsoft.netting.im.application.event.IMEventCodes;
import com.dobbinsoft.netting.server.event.IOEvent;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizeResultEvent implements IOEvent {

    private Boolean success;

    private List<EventCause> causes;

    @Override
    public int eventCode() {
        return IMEventCodes.AUTHORIZE_RESULT;
    }

}
