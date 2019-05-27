package com.pulmuone.demo.api.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

//@Configuration
@MapperScan(
        basePackages="com.pulmuone.demo",
        sqlSessionFactoryRef = "mysqlSessionFactory",
        sqlSessionTemplateRef = "mysqlSessionTemplate")
public class DataSourceConfig {

    @Bean(name="mysqlDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dbDataSource()
    {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="mysqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("mysqlDataSource")DataSource dataSource) throws Exception{
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();

        sessionFactoryBean.setDataSource(dataSource);
        // annotation이 아닌 xml을 통한 mapping을 할거면 아래와 같이 사용
        //PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //sessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mybatis/mapper/**/*.xml"));


        return sessionFactoryBean.getObject();
    }

    @Bean(name="mysqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
