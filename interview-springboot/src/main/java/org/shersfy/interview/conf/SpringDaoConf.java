package org.shersfy.interview.conf;

import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.jolbox.bonecp.BoneCPDataSource;

@SpringBootConfiguration
@ComponentScan(basePackages="org.shersfy.interview")
@PropertySource(value={"classpath:config.properties"})
public class SpringDaoConf {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(SpringDaoConf.class);
	
	@Value("${jdbcDriverClass}")
	private String jdbcDriverClass;
	
	@Value("${jdbcMasterUrl}")
	private String jdbcMasterUrl;
	@Value("${jdbcMasterUsername}")
	private String jdbcMasterUsername;
	@Value("${jdbcMasterPassword}")
	private String jdbcMasterPassword;
	
	@Value("${jdbcMaxConnectionsPerPartition}")
	private int jdbcMaxConnectionsPerPartition;
	@Value("${jdbcMinConnectionsPerPartition}")
	private int jdbcMinConnectionsPerPartition;
	@Value("${jdbcPartitionCount}")
	private int jdbcPartitionCount;
	
	@Value("${jdbcIdleConnectionTestPeriod}")
	private long jdbcIdleConnectionTestPeriod;
	@Value("${jdbcIdleMaxAge}")
	private long jdbcIdleMaxAge;
	
	@Value("${jdbcAcquireIncrement}")
	private int jdbcAcquireIncrement;
	@Value("${jdbcStatementsCacheSize}")
	private int jdbcStatementsCacheSize;
	@Value("${jdbcConnectionTimeout}")
	private int jdbcConnectionTimeout;

	@Bean
	public DataSource dataSource(){
//		<property name="jdbcUrl" value="${jdbcMasterUrl}" />
//		<property name="username" value="${jdbcMasterUsername}" />
//		<property name="password" value="${jdbcMasterPassword}" />
//		<property name="driverClass" value="${jdbcDriverClass}" />
//		<property name="maxConnectionsPerPartition" value="${jdbcMaxConnectionsPerPartition}" />
//		<property name="minConnectionsPerPartition" value="${jdbcMinConnectionsPerPartition}" />
//		<property name="idleConnectionTestPeriodInMinutes" value="${jdbcIdleConnectionTestPeriod}" />
//		<property name="idleMaxAgeInMinutes" value="${jdbcIdleMaxAge}" />
//		<property name="partitionCount" value="${jdbcPartitionCount}" />
//		<property name="acquireIncrement" value="${jdbcAcquireIncrement}" />
//		<property name="statementsCacheSize" value="${jdbcStatementsCacheSize}" />
//		<property name="connectionTimeoutInMs" value="${jdbcConnectionTimeout}" />
		
		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setJdbcUrl(jdbcMasterUrl);
		dataSource.setUsername(jdbcMasterUsername);
		dataSource.setPassword(jdbcMasterPassword);
		dataSource.setDriverClass(jdbcDriverClass);
		dataSource.setMaxConnectionsPerPartition(jdbcMaxConnectionsPerPartition);
		dataSource.setMinConnectionsPerPartition(jdbcMinConnectionsPerPartition);
		dataSource.setPartitionCount(jdbcPartitionCount);
		dataSource.setIdleConnectionTestPeriod(jdbcIdleConnectionTestPeriod, TimeUnit.SECONDS);
		dataSource.setIdleMaxAge(jdbcIdleMaxAge, TimeUnit.SECONDS);
		dataSource.setAcquireIncrement(jdbcAcquireIncrement);
		dataSource.setStatementsCacheSize(jdbcStatementsCacheSize);
		dataSource.setConnectionTimeout(jdbcConnectionTimeout, TimeUnit.SECONDS);
		
		return dataSource;
	}
	
	@Bean
	public SqlSessionFactory sessionFactory() throws Exception{
//		<property name="dataSource" ref="dataSource" />
//		<property name="configLocation" value="classpath:mapper/mapper.xml" />
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
//		sessionFactory.setConfigLocation(configLocation);
		return sessionFactory.getObject();
	}
	
	@Bean
	public SqlSession sqlSession(){
		SqlSession sqlSession = null;
		try {
			sqlSession = new SqlSessionTemplate(sessionFactory());
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return sqlSession;
	}
}
