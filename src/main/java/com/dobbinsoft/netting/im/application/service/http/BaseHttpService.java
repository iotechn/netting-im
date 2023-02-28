package com.dobbinsoft.netting.im.application.service.http;

import com.dobbinsoft.netting.im.exception.ImErrorCode;
import com.dobbinsoft.netting.im.exception.ImException;
import com.dobbinsoft.netting.im.web.HttpServiceRouter;
import lombok.extern.slf4j.Slf4j;

/**
 * HttpService 为开放出去的，相当于管理端API
 */
@Slf4j
public abstract class BaseHttpService {

    public String emptyMethod(String string) {
        throw new ImException(ImErrorCode.API_NOT_EXIST);
    }

    public abstract String group();

}
