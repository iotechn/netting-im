package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.JwtUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.base.utils.StringUtils;
import com.dobbinsoft.netting.server.cluster.ClusterNodeSynchronizer;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNodeEvent;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.inner.AuthorizedInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;


@Slf4j
@Singleton
public class AuthorizedInnerEventHandler extends AbstractInnerEventHandler<AuthorizedInnerEvent> {

    @Inject
    private TerminalRepository terminalRepository;

    @Inject
    private ClusterNodeSynchronizer clusterNodeSynchronizer;

    @Override
    public void handle(AuthorizedInnerEvent innerEvent, Terminal terminal) {
        if (terminal != null) {
            terminal.setJwtToken(innerEvent.getJwtToken());
            String publicKey = PropertyUtils.getProperty("server.ws.auth.public-key");
            JwtUtils.JwtResult jwtResult = JwtUtils.verifyRSA256(terminal.getJwtToken(), publicKey);
            if (jwtResult.getResult() == JwtUtils.Result.SUCCESS) {
                Map<String, String> payload = jwtResult.getPayload();
                String businessUserId = payload.get("businessUserId");
                terminal.setBusinessUserId(businessUserId);
                String permissionKeys = payload.get("permissionKeys");
                if (StringUtils.isNotEmpty(permissionKeys)) {
                    terminal.setPermissionKeys(Arrays.asList(permissionKeys.split(",")));
                } else {
                    terminal.setPermissionKeys(Arrays.asList());
                }
                terminalRepository.save(terminal);
                ClusterNodeEvent clusterNodeEvent = new ClusterNodeEvent();
                clusterNodeEvent.setEvent(ClusterNodeEvent.TERMINAL_AUTHORIZED);
                clusterNodeEvent.setBusinessUserId(terminal.getBusinessUserId());
                clusterNodeEvent.setClusterNode(ClusterNodeSynchronizer.clusterNode);
                clusterNodeSynchronizer.sendEvent(clusterNodeEvent);
            } else {
                log.warn("[Authorized Event] verifyRSA256 token failed, token: {}", terminal.getJwtToken());
            }
        } else {
            log.error("[Authorized Event] terminal is null, event={}", JsonUtils.toJson(innerEvent));
        }
    }

    @Override
    public Class<AuthorizedInnerEvent> eventClass() {
        return AuthorizedInnerEvent.class;
    }
}
