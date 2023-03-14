package com.dobbinsoft.netting.im.infrastructure.po;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserFieldPO {

    private Long id;

    private String businessUserId;

    private String fieldName;

    private String fieldValue;

    private LocalDateTime createTime;

}
