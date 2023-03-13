package com.dobbinsoft.netting.im.application.service.http;

import com.dobbinsoft.netting.base.utils.CollectionUtils;
import com.dobbinsoft.netting.im.application.service.http.dto.UserDeleteDTO;
import com.dobbinsoft.netting.im.application.service.http.dto.UserImportDTO;
import com.dobbinsoft.netting.im.domain.entity.User;
import com.dobbinsoft.netting.im.domain.repository.UserRepository;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class UserHttpService extends BaseHttpService {

    @Inject
    private UserRepository userRepository;

    public List<String> batchImportUser(UserImportDTO userImportDTO) {
        List<UserImportDTO.UserDTO> users = userImportDTO.getUsers();
        if (CollectionUtils.isEmpty(users)) {
            return Collections.emptyList();
        }
        List<User> entities = users.stream().map(dto -> {
            User user = new User();
            user.setBusinessUserId(dto.getBusinessUserId());
            user.setUserSecret(dto.getUserSecret());
            return user;
        }).collect(Collectors.toList());
        return userRepository.saveUserBatch(entities);
    }

    public Integer batchDeleteUser(UserDeleteDTO userDeleteDTO) {
        return userRepository.deleteUserBatch(userDeleteDTO.getBusinessUserIds());
    }

    @Override
    public String group() {
        return "user";
    }

}
