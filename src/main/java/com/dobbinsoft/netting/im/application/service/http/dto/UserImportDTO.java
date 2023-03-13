package com.dobbinsoft.netting.im.application.service.http.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserImportDTO {

    private List<UserDTO> users;

    @Getter
    @Setter
    public static class UserDTO {

        private String businessUserId;

        private String userSecret;

    }

}
