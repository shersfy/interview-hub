package org.shersfy.framework.mvc;

/**
 * 视图层，视图代表模型包含数据的可视化。
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class View {
	
	public void showDetail(Model model) {
		if(model == null) {
			System.out.println("model is null");
			return;
		}
		System.out.println("===========model start==============");
		System.out.println(String.format("id: %s", model.getId()));
		System.out.println(String.format("name: %s", model.getName()));
		System.out.println(String.format("time: %s", model.getTime().getTime()));
		System.out.println("===========model end==============");
	}

}
