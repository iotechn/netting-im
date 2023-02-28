package com.dobbinsoft.netting.im.web.vo;

import com.dobbinsoft.netting.im.exception.ImErrorCode;
import com.dobbinsoft.netting.im.exception.ImException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebResult<T> {

    private int code = 0;

    private T data;

    private String message;

    private String messageEn;

    private String traceId;

    public static <T> WebResult<T> success(T t) {
        WebResult<T> webResult = new WebResult<>();
        webResult.setData(t);
        webResult.setCode(ImErrorCode.SUCCESS.getCode());
        webResult.setMessage(ImErrorCode.SUCCESS.getMsg());
        webResult.setMessageEn(ImErrorCode.SUCCESS.getMsgEN());
        return webResult;
    }

    public static WebResult error(ImErrorCode errorCode) {
        WebResult webResult = new WebResult<>();
        webResult.setData(null);
        webResult.setCode(errorCode.getCode());
        webResult.setMessage(errorCode.getMsg());
        webResult.setMessageEn(errorCode.getMsgEN());
        return webResult;
    }

    public static WebResult fail(ImException imException) {
        WebResult webResult = new WebResult<>();
        webResult.setData(null);
        webResult.setCode(imException.getCode());
        webResult.setMessage(imException.getMessage());
        webResult.setMessageEn(imException.getMessageEn());
        return webResult;
    }

}
