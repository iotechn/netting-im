package com.dobbinsoft.netting.im.infrastructure.mapper;

import com.dobbinsoft.netting.im.infrastructure.po.UserFieldPO;

import java.util.List;

public interface UserFieldMapper {

    public List<UserFieldPO> findByBusinessUserId(String businessUserId);

}
