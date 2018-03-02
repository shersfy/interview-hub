package org.shersfy.build.factory;

/**
 * 工厂模式（Factory Pattern）<br/>
 * 在工厂模式中，我们在创建对象时不会对客户端暴露创建逻辑，并且是通过使用一个共同的接口来指向新创建的对象。<br/>
 * 
 * 使用一个共同的接口创建不同的对象, 不对外暴露创建逻辑。<br/>
 * 如SqlSessionFactory.buildSession(String type)， 只调用接口，传递参数信息，就能得到想要的对象。<br/>
 * 
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class SqlSessionFactory {

	public static void main(String[] args) {
		SqlSessionFactory factory = new SqlSessionFactory();
		SqlSession session = factory.buildSession("mysql");
		session.delete("");
		session = factory.buildSession("oracle");
		session.delete("");
		session = factory.buildSession("mssql");
		session.delete("");
		
	}
	
	public SqlSession buildSession(String type) {
		if("mysql".equalsIgnoreCase(type)) {
			return new MySQLSession();
		}
		if("oracle".equalsIgnoreCase(type)) {
			return new OracleSession();
		}
		if("mssql".equalsIgnoreCase(type)) {
			return new MSSQLSession();
		}
		
		return null;
	}
}
