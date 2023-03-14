package com.dobbinsoft.netting.im.domain.entity;

public class BaseEntity {

    protected Status entityStatus = Status.Persist;

    public void setEntityStatus(Status entityStatus) {
        this.entityStatus = entityStatus;
    }

    public enum Status {
        // 对象处于瞬时态
        Transient,
        // 对象处于持久态
        Persist,
    }

}
