package com.dobbinsoft.netting.im.infrastructure.po;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserRelationPO {

    private Long id;

    private String businessUserId;

    private String targetBusinessUserId;

    /**
     * 1. Friend
     * 2. Black
     */
    private Integer type;

    private LocalDateTime createTime;

}
