package com.dobbinsoft.netting.im.infrastructure.mapper;

import com.dobbinsoft.netting.im.infrastructure.po.GroupPO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface GroupMapper {

//    @Select("select 1 as id")
    public GroupPO findById(@Param("id") Long id);

}
