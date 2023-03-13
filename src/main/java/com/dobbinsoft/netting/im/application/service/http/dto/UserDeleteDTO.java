package com.dobbinsoft.netting.im.application.service.http.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDeleteDTO {

    private List<String> businessUserIds;

}
