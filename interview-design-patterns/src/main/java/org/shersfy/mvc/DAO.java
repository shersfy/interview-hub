package org.shersfy.mvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据库查询服务
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class DAO {
	
	private List<Model> list;
	
	public DAO() {
		list = new ArrayList<>();
		
		for(int id=1; id<=10; id++) {
			Model model = new Model(id);
			model.setName("modelname"+id);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			model.setTime(new Date());
			list.add(model);
		}
	}
	
	public Model findById(int id) {
		for(Model model :list) {
			if(model.getId() == id) {
				return model;
			}
		}
		
		return null;
	}

}
