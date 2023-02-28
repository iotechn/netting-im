package com.dobbinsoft.netting.server.domain.entity;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Terminal {

    private String id;

    /**
     * JWT Token
     */
    private String jwtToken;

    /**
     * WebSocket Channel
     */
    private Channel channel;


    /**
     * 业务绑定的BusinessUserId
     */
    private String businessUserId;

    /**
     * Terminal可以产生的权限KEY
     */
    private List<String> permissionKeys;

}
