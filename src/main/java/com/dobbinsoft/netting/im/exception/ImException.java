package com.dobbinsoft.netting.im.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImException extends RuntimeException {

    private int code;

    private String message;

    private String messageEn;

    public ImException(int code, String message, String messageEn) {
        super(code + "|" + message + "|" + messageEn);
        this.code = code;
        this.message = message;
        this.messageEn = messageEn;
    }

    public ImException(ImErrorCode imErrorCode) {
        super(imErrorCode.getCode() + "|" + imErrorCode.getMsg() + "|" + imErrorCode.getMsgEN());
        this.code = imErrorCode.getCode();
        this.message = imErrorCode.getMsg();
        this.messageEn = imErrorCode.getMsgEN();
    }

}
