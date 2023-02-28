package com.dobbinsoft.netting.im.infrastructure.po;

import lombok.Getter;
import lombok.Setter;

/**
 * @author w.wei
 * @version 1.0
 * @description: GroupUserPO
 * @date 2023/2/23
 */
@Getter
@Setter
public class GroupUserPO {

    private Long id;

    private Long groupId;

    private Long userId;

    private String remark;

}
