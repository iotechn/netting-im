package com.dobbinsoft.netting.im.infrastructure.mapper;

import com.dobbinsoft.netting.im.infrastructure.po.GroupUserPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupUserMapper {

    public List<GroupUserPO> findByGroupId(Long groupId);

    public List<GroupUserPO> findByGroupIdAndUserId(@Param("groupId") Long groupId, @Param("userId") Long userId);

}
