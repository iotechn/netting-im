package com.dobbinsoft.netting;

import com.dobbinsoft.netting.im.infrastructure.ioc.module.GuiceModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class IOC {

    public static final Injector INSTANCE = Guice.createInjector(new GuiceModule());

}
