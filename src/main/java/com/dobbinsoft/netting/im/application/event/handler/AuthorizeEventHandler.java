package com.dobbinsoft.netting.im.application.event.handler;

import com.dobbinsoft.netting.base.ext.CaughtCallable;
import com.dobbinsoft.netting.base.utils.JwtUtils;
import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.im.application.event.AbstractEventHandler;
import com.dobbinsoft.netting.im.application.event.EventCause;
import com.dobbinsoft.netting.im.application.event.IMEventCodes;
import com.dobbinsoft.netting.im.application.event.event.AuthorizeEvent;
import com.dobbinsoft.netting.im.application.event.event.AuthorizeResultEvent;
import com.dobbinsoft.netting.im.domain.entity.User;
import com.dobbinsoft.netting.im.domain.repository.UserRepository;
import com.dobbinsoft.netting.server.event.inner.AuthorizedInnerEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Singleton
public class AuthorizeEventHandler extends AbstractEventHandler<AuthorizeEvent> {

    @Inject
    private UserRepository userRepository;

    @Override
    public Future<List<String>> handle(AuthorizeEvent authorizeEvent, String jwtToken) {
        Future<List<String>> submit = defaultEventLoopGroup.submit(new CaughtCallable<List<String>>() {
            @Override
            public List<String> caughtCall() {
                String privateKey = PropertyUtils.getProperty("server.im-api.auth.private-key");
                User user = userRepository.findByBusinessUserId(authorizeEvent.getBusinessUserId());
                if (user != null && user.getUserSecret().equals(authorizeEvent.getUserSecret())) {
                    HashMap<String, String> payload = new HashMap<>();
                    payload.put("businessUserId", authorizeEvent.getBusinessUserId());
                    try {
                        String newJwtToken = JwtUtils.createRSA256(new HashMap<String, String>(), payload, 60 * 60 * 24 * 30, privateKey);
                        AuthorizedInnerEvent authorizedInnerEvent = new AuthorizedInnerEvent();
                        authorizedInnerEvent.setJwtToken(newJwtToken);

                        AuthorizeResultEvent authorizeResultEvent = new AuthorizeResultEvent(true, null);
                        return Arrays.asList(authorizedInnerEvent.toMessage(), authorizeResultEvent.toMessage());
                    } catch (InvalidKeySpecException e) {
                        log.error("[IM Event] Authorize rsa256 private key invalid!");
                        AuthorizeResultEvent authorizeResultEvent = new AuthorizeResultEvent(false, Arrays.asList(EventCause.AUTHORIZED_INVALID_PRIVATE_KEY));
                        return Arrays.asList(authorizeResultEvent.toMessage());
                    }
                }
                AuthorizeResultEvent authorizeResultEvent = new AuthorizeResultEvent(false, Arrays.asList(EventCause.AUTHORIZED_SECRET_INCORRECT));
                return Arrays.asList(authorizeResultEvent.toMessage());
            }
        });
        return submit;
    }

    @Override
    public Class<AuthorizeEvent> eventClass() {
        return AuthorizeEvent.class;
    }

    @Override
    public int eventCode() {
        return IMEventCodes.AUTHORIZE;
    }

}
