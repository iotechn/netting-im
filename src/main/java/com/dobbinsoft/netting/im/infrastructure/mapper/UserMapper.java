package com.dobbinsoft.netting.im.infrastructure.mapper;

import com.dobbinsoft.netting.im.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author w.wei
 * @version 1.0
 * @description: UserMapper
 * @date 2023/2/23
 */
public interface UserMapper {

    public UserPO findByBusinessId(String businessUserId);

    public List<UserPO> findByBusinessIds(@Param("businessUserIds") Collection<String> businessUserIds);

    public Integer insertBatch(@Param("pos") List<UserPO> pos);

    public Integer updateBatch(@Param("pos")List<UserPO> toUpdateList);

    public Integer deleteBatch(@Param("businessIds")List<String> businessIds);

}
