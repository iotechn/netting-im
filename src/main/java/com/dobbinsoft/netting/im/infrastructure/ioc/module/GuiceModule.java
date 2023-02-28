package com.dobbinsoft.netting.im.infrastructure.ioc.module;

import com.dobbinsoft.netting.base.utils.PropertyUtils;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        this.install(new MyBatisModule() {
            @Override
            protected void initialize() {
                Names.bindProperties(binder(), PropertyUtils.properties);
                bindDataSourceProviderType(PooledDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                addMapperClasses("com.dobbinsoft.netting.im.infrastructure.mapper");
            }
        });
    }
}
