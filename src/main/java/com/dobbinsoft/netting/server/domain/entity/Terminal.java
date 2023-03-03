package com.dobbinsoft.netting.server.domain.entity;

import com.dobbinsoft.netting.server.protocol.ProtocolWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class Terminal {

    public Terminal(String id) {
        this.id = id;
    }

    /**
     * must not null
     */
    private String id;

    /**
     * Channel must not null
     */
    private Channel channel;

    /**
     * Terminal connected which machine
     * not null
     */
    private String machineIp;

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

    /**
     * 目前Terminal关注的Group
     */
    private StreamGroup currentGroup;

    /**
     * Terminal 关注一个 Group
     * @param streamGroup
     */
    public void followStreamGroup(StreamGroup streamGroup) {
        streamGroup.getMembers().add(this);
        this.currentGroup = streamGroup;
    }

    public void unfollowStreamGroup() {
        if (this.currentGroup != null) {
            this.currentGroup.getMembers().remove(this);
            this.currentGroup = null;
        }
    }

    /**
     * 发送流
     * @param byteBuf
     */
    public void sendStream(ByteBuf byteBuf) {
        Set<Terminal> members = this.currentGroup.getMembers();
        if (members.size() > 1) {
            for (Terminal member : members) {
                if (!member.equals(this)) {
                    member.getChannel().writeAndFlush(member.getProtocolWrapper().wrapBytes(byteBuf));
                }
            }
        }
    }

    public boolean isTheSameMachine(Terminal terminal) {
        return this.machineIp.equals(terminal.getMachineIp());
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return super.hashCode();
        }
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Terminal) {
            if (this.id.equals(((Terminal) obj).getId())) {
                return true;
            }
        }
        return false;
    }
}
