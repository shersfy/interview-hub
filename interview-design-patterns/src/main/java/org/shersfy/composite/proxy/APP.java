package org.shersfy.composite.proxy;

import org.shersfy.composite.proxy.dynamic.DynamicProxyHandler;

/**
 * 代理模式（Proxy Pattern）<br/>
 * 意图：为其他对象(USServer)提供一种代理(ProxyBJServer)以控制对这个对象(USServer)的访问。<br/>
 * 
 * 主要解决：在直接访问对象时带来的问题，比如说：要访问的对象在远程的机器上。
 * 在面向对象系统中，有些对象由于某些原因（比如对象创建开销很大，或者某些操作需要安全控制，或者需要进程外的访问），
 * 直接访问会给使用者或者系统结构带来很多麻烦，我们可以在访问此对象时加上一个对此对象的访问层。
 * 关键代码：实现与被代理类组合。<br/>
 * 优点： 1、职责清晰。 2、高扩展性。 3、智能化。 <br/>
 * 缺点： 1、由于在客户端和真实主题之间增加了代理对象，因此有些类型的代理模式可能会造成请求的处理速度变慢。 2、实现代理模式需要额外的工作，有些代理模式的实现非常复杂。<br/> 
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class APP {

	public static void main(String[] args) {
		System.out.println("========直接访问美国服务器上的资源, 未授权无法访问==============");
		String logo = null;
		USServer us = new USServer();
		logo = us.accessResource();
		
		System.out.println("========使用北京代理服务器访问, 代理服务器已授权可以访问==============");
		ProxyBJServer bj = new ProxyBJServer();
		logo = bj.accessResource();
		System.out.println(logo);
		
		System.out.println("========使用动态代理==============");
		String token = "dl24nfew3";
		DynamicProxyHandler proxy = new DynamicProxyHandler(token);
		logo = proxy.accessResource();
		System.out.println(logo);
	}
}
