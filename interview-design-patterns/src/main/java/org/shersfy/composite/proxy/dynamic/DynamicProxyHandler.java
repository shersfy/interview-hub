package org.shersfy.composite.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.shersfy.composite.proxy.MainProxy;
import org.shersfy.composite.proxy.IServer;
import org.shersfy.composite.proxy.USServer;

/**
 * 动态代理<br/>
 * 主要通过java.lang.reflect.Proxy类和java.lang.reflect.InvocationHandler接口。 
 * Proxy类主要用来获取动态代理对象，InvocationHandler接口用来约束调用者实现<br/>
 * 代理可以访问实际对象，但是延迟实现实际对象的部分功能，实际对象实现系统的实际功能，代理对象对客户隐藏了实际对象。<br/>
 * 三个元素： 
 * 调用处理类 DynamicProxyHandler; 
 * 代理接口 IServer; 
 * 被代理实例 new USServer(token)
 * @author shersfy
 * @date 2018-03-01
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class DynamicProxyHandler implements InvocationHandler, IServer{

	private String token;
	
	public DynamicProxyHandler(String token) {
		this.token = token;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("proxy: "+proxy.getClass().getName());
		return method.invoke(new USServer(token), args);
	}


	@Override
	public String accessResource() {
		// 使用动态代理
		// 这句话执行后返回一个Proxy对象，该对象是通过java反射机制动态生成一个已实现接口IServer的类的实例。
		// 并且重写了接口里面的方法（也就是说代理类与被代理类有相同的接口）
		IServer proxy = (IServer) Proxy.newProxyInstance(MainProxy.class.getClassLoader(), 
				new Class[] {IServer.class}, 
				this);
		return proxy.accessResource();
	}
	
	

}
