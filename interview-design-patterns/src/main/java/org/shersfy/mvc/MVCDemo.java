package org.shersfy.mvc;

/**
 * MVC 模式代表 Model-View-Controller（模型-视图-控制器） 模式。这种模式用于应用程序的分层开发。<br/>
 * 主要应用于软件应用的分层开发和框架的模型架构。
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class MVCDemo {
	
	public static void main(String[] args) {
		Controller control = new Controller();
		View view = new View();
		
		int id = 5;
		view.showDetail(control.getDetail(id));
	}

}
