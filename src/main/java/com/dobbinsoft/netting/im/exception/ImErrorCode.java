package com.dobbinsoft.netting.im.exception;

public enum ImErrorCode {

    // 系统错误码 < 10000
    SUCCESS(0, "成功", "Success"),
    API_NOT_EXIST(404, "API不存在", "Api not exists!"),
    HTTP_NOT_SUPPORT(405, "Http Method 不支持", "Http method is not supported!"),
    SERVICE_ERROR(500, "业务异常", "Service error"),
    VALIDATOR_ERROR(501,"参数检查异常","Check error"),
    SESSION_ERROR(502, "权限异常", "Permission error"),

    ;
    private int code;
    private String msg;
    private String msgEN;

    ImErrorCode(int code, String msg, String msgEN) {
        this.code = code;
        this.msg = msg;
        this.msgEN = msgEN;
    }

    public String getMsgEN() {
        return this.msgEN;
    }

    public int getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}

