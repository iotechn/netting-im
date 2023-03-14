package com.dobbinsoft.netting.im.infrastructure.mapper;

import com.dobbinsoft.netting.im.infrastructure.po.UserRelationPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserRelationMapper {

    public List<UserRelationPO> findRelations(@Param("businessUserId") String businessUserId, @Param("type") Integer type);

}
