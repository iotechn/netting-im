package com.dobbinsoft.netting.im.application.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventCause {

    private int code;

    private String cause;

    public static final EventCause AUTHORIZED_SECRET_INCORRECT = EventCause.newCause(1001, "User id or secret is incorrect!");

    public static final EventCause AUTHORIZED_INVALID_PRIVATE_KEY = EventCause.newCause(1002, "The ras256 private key is invalid!");

    public static EventCause newCause(int code, String cause) {
        EventCause eventCause = new EventCause();
        eventCause.code = code;
        eventCause.cause = cause;
        return eventCause;
    }

}
