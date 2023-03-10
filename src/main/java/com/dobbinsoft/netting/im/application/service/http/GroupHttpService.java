package com.dobbinsoft.netting.im.application.service.http;

import com.dobbinsoft.netting.adapter.event.JvmEventDispatcher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GroupHttpService extends BaseHttpService {

    @Inject
    private JvmEventDispatcher jvmEventDispatcher;

    @Override
    public String group() {
        return "group";
    }

    public String hello(String body) {
        jvmEventDispatcher.dispatchToTerminal("1003|{}", "bu2");
        jvmEventDispatcher.dispatchToTerminal("1003|{}", "bu1");
        return "world";
    }

}
