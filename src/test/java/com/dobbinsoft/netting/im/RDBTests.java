package com.dobbinsoft.netting.im;

import com.dobbinsoft.netting.im.infrastructure.ioc.module.GuiceModule;
import com.dobbinsoft.netting.im.infrastructure.mapper.GroupMapper;
import com.dobbinsoft.netting.im.infrastructure.po.GroupPO;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author w.wei
 * @version 1.0
 * @description: RDBTests
 * @date 2023/2/23
 */
public class RDBTests {

    private Injector injector = null;

    @Before
    public void before(){
        System.out.println(System.currentTimeMillis());
        injector = Guice.createInjector(new GuiceModule());
        System.out.println("before方法执行结束");
    }

    @Test
    public void selectGroupById() {
        System.out.println("查询数据的方法开始执行");
        //获取会话
        //代理模式  代理对象需要依赖于原始调用方法
        //通过接口获取对象实例
        GroupMapper groupMapper = injector.getInstance(GroupMapper.class);
        GroupMapper groupMapper2 = injector.getInstance(GroupMapper.class);

        System.out.println(System.currentTimeMillis());
        //测试接口
        GroupPO po = groupMapper.findById(1L);
        System.out.println(po);
    }

    @After
    public void  after() {
        System.out.println("after方法执行结束");
    }

}
