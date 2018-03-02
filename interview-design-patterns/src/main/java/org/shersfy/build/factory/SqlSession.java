package org.shersfy.build.factory;

public interface SqlSession {
	
	public Object selectOne(String sql);
	public int delete(String sql);
	public int update(String sql);
	public int insert(String statement);

}
