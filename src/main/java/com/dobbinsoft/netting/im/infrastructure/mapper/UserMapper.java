package com.dobbinsoft.netting.im.infrastructure.mapper;

import com.dobbinsoft.netting.im.infrastructure.po.UserPO;

import java.util.Collection;
import java.util.List;

/**
 * @author w.wei
 * @version 1.0
 * @description: UserMapper
 * @date 2023/2/23
 */
public interface UserMapper {

    public List<UserPO> findByIds(Collection<Long> userIds);

}
