package com.dobbinsoft.netting.server.domain.entity;

import com.dobbinsoft.netting.server.protocol.ProtocolWrapper;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Terminal {

    /**
     * must not null
     */
    private String id;

    /**
     * Channel must not null
     */
    private Channel channel;

    /**
     * must not null
     */
    private ProtocolWrapper protocolWrapper;

    /**
     * JWT Token
     */
    private String jwtToken;

    /**
     * 业务绑定的BusinessUserId
     */
    private String businessUserId;

    /**
     * Terminal可以产生的权限KEY
     */
    private List<String> permissionKeys;

}
