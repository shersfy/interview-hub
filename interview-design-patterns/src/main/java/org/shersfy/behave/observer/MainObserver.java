package org.shersfy.behave.observer;

import java.util.Scanner;

/**
 * 观察者模式（Observer Pattern）<br/>
 * 定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。<br/>
 * 例：<br/>
 * QQ群，好友既是观察者也是被观察者，当被观察者发送消息时，所有的其它成员都收到被观察者发送的信息。<br/>
 * 
 * @author shersfy
 * @date 2018-03-02
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class MainObserver {

	public static void main(String[] args) {

		// 被观察者
		QQSubject subj = new QQSubject("张三");
		
		// 观察者
		QQObserver obs1 = new QQObserver("李四");
		QQObserver obs2 = new QQObserver("王五");
		QQObserver obs3 = new QQObserver("赵六");
		
		subj.getGroup().add(obs1);
		subj.getGroup().add(obs2);
		subj.getGroup().add(obs3);
		
		// 被观察者发送消息
		System.out.println(String.format("我叫%s, 输入：", subj.getNikeName()));
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNextLine()) {
			String msg = scanner.nextLine();
			subj.sendMsg(msg);
		}
			
		scanner.close();
	}

}
