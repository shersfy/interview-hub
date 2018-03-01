package org.shersfy.framework.mvc;

/**
 *  控制层，控制器作用于模型和视图上。它控制数据流向模型对象，并在数据变化时更新视图。它使视图与模型分离开。
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Controller {
	
	private DAO dao;
	
	public Controller() {
		dao = new DAO();
	}
	
	public Model getDetail(int id) {
		return dao.findById(id);
	}

}
