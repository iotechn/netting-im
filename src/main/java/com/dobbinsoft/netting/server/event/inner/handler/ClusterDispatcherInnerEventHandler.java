package com.dobbinsoft.netting.server.event.inner.handler;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.JwtUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.server.domain.entity.Terminal;
import com.dobbinsoft.netting.server.domain.repository.TerminalRepository;
import com.dobbinsoft.netting.server.event.inner.ClusterDispatcherInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class ClusterDispatcherInnerEventHandler extends AbstractInnerEventHandler<ClusterDispatcherInnerEvent> {

    @Inject
    private TerminalRepository terminalRepository;

    @Override
    public void handle(ClusterDispatcherInnerEvent clusterDispatcherInnerEvent, Terminal nullTerminal) {
        String businessUserId = clusterDispatcherInnerEvent.getBusinessUserId();
        JwtUtils.JwtResult jwtResult = JwtUtils.verifyHMAC256(clusterDispatcherInnerEvent.getSign(), PropertyUtils.getProperty("server.cluster.sign-key"));
        if (jwtResult.getResult() == JwtUtils.Result.SUCCESS
                && jwtResult.getPayload().get("businessUserId").equals(businessUserId)) {
            Terminal terminal = terminalRepository.findByBusinessUserId(businessUserId);
            if (terminal != null) {
                terminal.getChannel().writeAndFlush(terminal.getProtocolWrapper().wrap(clusterDispatcherInnerEvent.getIoEvent()));
            } else {
                log.error("[ClusterDispatcher] terminal is null, event={}", JsonUtils.toJson(clusterDispatcherInnerEvent));
            }
        } else {
            log.error("[ClusterDispatcher] Jwt verify failed !!! event={}", JsonUtils.toJson(clusterDispatcherInnerEvent));
        }

    }

    @Override
    public Class<ClusterDispatcherInnerEvent> eventClass() {
        return ClusterDispatcherInnerEvent.class;
    }

}
