package org.shersfy.build.factory;

public class MSSQLSession implements SqlSession{

	@Override
	public Object selectOne(String sql) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int delete(String sql) {
		System.out.println(this.getClass().getName() + " delete 1 record");
		return 0;
	}

	@Override
	public int update(String sql) {
		return 0;
	}

	@Override
	public int insert(String statement) {
		// TODO Auto-generated method stub
		return 0;
	}

}
