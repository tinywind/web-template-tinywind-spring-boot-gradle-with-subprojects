package org.tinywind.server.__secondary_datasource.config;

import org.tinywind.server.config.MybatisConfig;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "org.tinywind.server.__secondary_datasource.repository", sqlSessionTemplateRef = "secondarySqlSession")
public class SecondaryPersistenceConfig {

    @Bean(name = "secondaryDataSource")
    @ConfigurationProperties(prefix = "user-data.datasource.secondary")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactoryBean(@Autowired @Qualifier("secondaryDataSource") DataSource secondaryDataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(secondaryDataSource);
        factoryBean.setConfiguration(MybatisConfig.createConfiguration());
        factoryBean.setMapperLocations(applicationContext.getResources("classpath:kr/co/klnet/tbs/repository/u.xml"));
        return factoryBean.getObject();
    }

    @Bean(name = "secondarySqlSession")
    public SqlSession secondarySqlSession(@Autowired @Qualifier("secondarySqlSessionFactory") SqlSessionFactory secondarySqlSessionFactory) {
        return new SqlSessionTemplate(secondarySqlSessionFactory);
    }

    @Bean(name = "secondaryTransactionManager")
    public DataSourceTransactionManager secondaryTransactionManager(@Autowired @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        return new DataSourceTransactionManager(secondaryDataSource);
    }

}

