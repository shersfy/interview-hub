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

import com.jolbox.bonecp.BoneCPDataSource;

@SpringBootConfiguration
//@PropertySource(value={"classpath:config.properties"})
public class SpringDaoConf {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(SpringDaoConf.class);
	
	@Value("${spring.datasource.driverClassName}")
	private String driverClassName;
	
	@Value("${spring.datasource.url}")
	private String url;
	
	@Value("${spring.datasource.username}")
	private String username;
	
	@Value("${spring.datasource.password}")
	private String password;
	
	@Value("${spring.datasource.maxConnectionsPerPartition}")
	private int maxConnectionsPerPartition;
	
	@Value("${spring.datasource.minConnectionsPerPartition}")
	private int minConnectionsPerPartition;
	
	@Value("${spring.datasource.partitionCount}")
	private int partitionCount;
	
	@Value("${spring.datasource.idleConnectionTestPeriod}")
	private long idleConnectionTestPeriod;
	
	@Value("${spring.datasource.idleMaxAge}")
	private long idleMaxAge;
	
	@Value("${spring.datasource.acquireIncrement}")
	private int acquireIncrement;
	
	@Value("${spring.datasource.statementsCacheSize}")
	private int statementsCacheSize;
	
	@Value("${spring.datasource.connectionTimeout}")
	private int connectionTimeout;
	
	public SpringDaoConf(){
	}

	@Bean
	public DataSource dataSource(){
		
		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClass(driverClassName);
		dataSource.setMaxConnectionsPerPartition(maxConnectionsPerPartition);
		dataSource.setMinConnectionsPerPartition(minConnectionsPerPartition);
		dataSource.setPartitionCount(partitionCount);
		dataSource.setIdleConnectionTestPeriod(idleConnectionTestPeriod, TimeUnit.SECONDS);
		dataSource.setIdleMaxAge(idleMaxAge, TimeUnit.SECONDS);
		dataSource.setAcquireIncrement(acquireIncrement);
		dataSource.setStatementsCacheSize(statementsCacheSize);
		dataSource.setConnectionTimeout(connectionTimeout, TimeUnit.SECONDS);
		
		return dataSource;
	}
	
	@Bean
	public SqlSessionFactory sessionFactory(DataSource dataSource) throws Exception{
		
		SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
//		sessionFactory.setConfigLocation(configLocation);
		return sessionFactory.getObject();
	}
	
	@Bean
	public SqlSession sqlSession(SqlSessionFactory sessionFactory){
		SqlSession sqlSession = null;
		try {
			sqlSession = new SqlSessionTemplate(sessionFactory);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return sqlSession;
	}
}
