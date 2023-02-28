package com.dobbinsoft.netting.server.event;

/**
 * @author w.wei
 * @version 1.0
 * @description: 抽象的IO事件，对于Server而言，
 * @date 2023/2/22
 */
public interface IOEvent {

    int INNER_EVENT_AUTHORIZED = -1;

    /**
     * 该事件所需的访问权限KEY
     * @return
     */
    default String permissionKey() {
        // 默认不需要访问权限
        return null;
    }

    /**
     * 事件类型 code，
     * 约定：
     * code > 0 为business定义的事件。由Business提供，Business解析
     * code < 0 为server内置事件，由business提供，server解析
     * @return
     */
    int eventCode();

}
