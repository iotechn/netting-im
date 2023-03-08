package com.dobbinsoft.netting;

import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.im.application.event.handler.AuthorizeEventHandler;
import com.dobbinsoft.netting.im.application.service.http.GroupHttpService;
import com.dobbinsoft.netting.im.infrastructure.ioc.module.GuiceModule;
import com.dobbinsoft.netting.im.web.HttpServiceRouter;
import com.dobbinsoft.netting.im.web.HttpWebServer;
import com.dobbinsoft.netting.server.cluster.ClusterHeartBeatPusher;
import com.dobbinsoft.netting.server.event.EventDispatcher;
import com.dobbinsoft.netting.server.event.IOEvent;
import com.dobbinsoft.netting.server.event.inner.handler.AbstractInnerEventHandler;
import com.dobbinsoft.netting.server.event.inner.handler.AuthorizedInnerEventHandler;
import com.dobbinsoft.netting.server.event.inner.handler.ClusterDispatcherInnerEventHandler;
import com.dobbinsoft.netting.server.event.inner.handler.StreamGroupJoinInnerEventHandler;
import com.dobbinsoft.netting.server.protocol.WebsocketServer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;


/**
 * @author w.wei
 * @version 1.0
 * @description: NettingBootstrap
 * @date 2023/2/22
 */
@Slf4j
public class NettingBootstrap {

    private static final int SERVER_COUNT = 2;

    public static void main(String[] args) throws Exception {
        // 1. Init Profile
        initProfile(args);
        Injector ioc = Guice.createInjector(new GuiceModule());
        CountDownLatch countDownLatch = new CountDownLatch(SERVER_COUNT);
        // 2. Guice IoC 无法进行预加载，部分对象必须进行预加载，所以这里手动列一下，哪些对象是要预加载的
        initIoC(ioc);
        // 3. 启动连接层
        bootServer(ioc, countDownLatch);
        // 4. 启动业务
        bootIm(ioc, countDownLatch);
        // 5. 启动集群间心跳
        bootCluster(ioc);
        countDownLatch.await();
        log.info("[System] shutdown");
    }

    private static void initProfile(String[] args) {
        String active = null;
        String network = null;
        for (String arg : args) {
            if (arg.startsWith("--active=")) {
                active = arg.replace("--active=", "");
            } else if (arg.startsWith("--network=")) {
                network = arg.replace("--network", "");
            }
        }
        PropertyUtils.init(active, network);
    }

    private static void initIoC(Injector ioc) {
        // 1. IM Http Api
        HttpServiceRouter.register(ioc.getInstance(GroupHttpService.class));
        // 2. IM Event Handler
        EventDispatcher.register(ioc.getInstance(AuthorizeEventHandler.class));
        // 3. Server Inner Handler
        AbstractInnerEventHandler.register(IOEvent.INNER_EVENT_AUTHORIZED, ioc.getInstance(AuthorizedInnerEventHandler.class));
        AbstractInnerEventHandler.register(IOEvent.INNER_EVENT_GROUP_STREAM_JOIN, ioc.getInstance(StreamGroupJoinInnerEventHandler.class));
        AbstractInnerEventHandler.register(IOEvent.INNER_EVENT_CLUSTER_DISPATCHER, ioc.getInstance(ClusterDispatcherInnerEventHandler.class));
    }

    private static void bootServer(Injector ioc, CountDownLatch countDownLatch) {
        new Thread(() -> {
            WebsocketServer websocketServer = ioc.getInstance(WebsocketServer.class);
            log.info("[Protocol] Websocket server startup!");
            websocketServer.doServer();
            log.info("[Protocol] Websocket server closed!");
            countDownLatch.countDown();
        }, "Thread-WS").start();
    }

    private static void bootIm(Injector ioc, CountDownLatch countDownLatch) {
        new Thread(() -> {
            HttpWebServer imWebServer = ioc.getInstance(HttpWebServer.class);
            log.info("[Business] IM managed api startup!");
            imWebServer.doServer();
            log.info("[Business] IM managed api closed!");
            countDownLatch.countDown();
        }, "Thread-IM").start();
    }

    private static void bootCluster(Injector ioc) {
        Boolean clusterServer = PropertyUtils.getPropertyBoolean("server.cluster");
        if (clusterServer) {
            new Thread(() -> {
                log.info("[Cluster] Heart beat pusher startup!");
                ClusterHeartBeatPusher clusterHeartBeatPusher = ioc.getInstance(ClusterHeartBeatPusher.class);
                clusterHeartBeatPusher.init();
            }, "Thread-HEART").start();
        }
    }

}
