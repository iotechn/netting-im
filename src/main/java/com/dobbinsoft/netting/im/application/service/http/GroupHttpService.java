package com.dobbinsoft.netting.im.application.service.http;

import javax.inject.Singleton;

@Singleton
public class GroupHttpService extends BaseHttpService {
    @Override
    public String group() {
        return "group";
    }

    public String hello(String body) {
        return "world";
    }

}
