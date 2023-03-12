package com.dobbinsoft.netting.cluster;

import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.dobbinsoft.netting.server.cluster.objects.ClusterNode;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.junit.Before;
import org.junit.Test;

public class ClusterTests {

    @Before
    public void before() {
        String active = null;
        String network = null;
        PropertyUtils.init(active, network);
    }

    @Test
    public void init() {
        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setHostAddress("192.168.123.181");
        clusterNode.setHostName("192.168.123.181");
        clusterNode.setWsServerPort(31364);
        clusterNode.init();
//        clusterNode.getChannel().writeAndFlush(new TextWebSocketFrame("hello"));
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
        }
    }

}
