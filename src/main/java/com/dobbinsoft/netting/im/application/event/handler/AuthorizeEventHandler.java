package com.dobbinsoft.netting.im.application.event.handler;

import com.dobbinsoft.netting.base.utils.JsonUtils;
import com.dobbinsoft.netting.base.utils.JwtUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.im.application.event.AbstractEventHandler;
import com.dobbinsoft.netting.im.application.event.AuthorizeEvent;
import com.dobbinsoft.netting.im.application.event.IMEventCodes;
import com.dobbinsoft.netting.server.event.inner.AuthorizedInnerEvent;
import com.google.inject.Singleton;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@Singleton
public class AuthorizeEventHandler extends AbstractEventHandler<AuthorizeEvent> {
    @Override
    public Class<AuthorizeEvent> eventClass() {
        return AuthorizeEvent.class;
    }

    @Override
    public int eventCode() {
        return IMEventCodes.AUTHORIZE;
    }

    static Map<String, String> mockDb = new HashMap<>();

    static {
        mockDb.put("hello", "world");
    }

    @Override
    public Future<List<String>> handle(AuthorizeEvent authorizeEvent, String jwtToken) {
        Future<List<String>> submit = defaultEventLoopGroup.submit(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                String privateKey = PropertyUtils.getProperty("server.im-api.auth.private-key");
                String userSecret = mockDb.get(authorizeEvent.getUserId());
                if (userSecret != null && userSecret.equals(authorizeEvent.getUserSecret())) {
                    HashMap<String, String> payload = new HashMap<>();
                    payload.put("businessUserId", authorizeEvent.getUserId());
                    try {
                        String newJwtToken = JwtUtils.createRSA256(new HashMap<String, String>(), payload, 60 * 60 * 24 * 30, privateKey);
                        AuthorizedInnerEvent authorizedInnerEvent = new AuthorizedInnerEvent();
                        authorizedInnerEvent.setJwtToken(newJwtToken);
                        return Arrays.asList(JsonUtils.toJson(authorizedInnerEvent));
                    } catch (InvalidKeySpecException e) {
                        log.error("[IM Event] Authorize rsa256 private key invalid!");
                    }
                }
                return null;
            }
        });
        return submit;
    }
}
