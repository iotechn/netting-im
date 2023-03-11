package com.dobbinsoft.netting.base.ext;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public abstract class CaughtCallable<V> implements Callable<V> {

    @Override
    public V call() {
        try {
            return caughtCall();
        } catch (Exception e) {
            log.error("[Caught Call] error", e);
        }
        return null;
    }

    public abstract V caughtCall();

}
