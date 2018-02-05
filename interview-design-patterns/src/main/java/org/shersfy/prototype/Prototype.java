package org.shersfy.prototype;

import java.util.Date;

/**
 * 原型模式<br/>
 * 这种类型的设计模式属于创建型模式，它提供了一种快速创建副本对象的最佳方式。<br/>
 * 优点：<br/>
 *  1、性能提高。<br/>
 *  2、逃避构造函数的约束。<br/>
 *  缺点：<br/>
 *  1、配备克隆方法需要对类的功能进行通盘考虑，这对于全新的类不是很难，但对于已有的类不一定很容易，特别当一个类引用不支持串行化的间接对象，
 *  或者引用含有循环结构的时候。 <br/>
 *  2、必须实现 Cloneable 接口。<br/>
 *  3、逃避构造函数的约束。<br/>
 *  使用场景：<br/>
 *  1、资源优化场景。 <br/>
 *  2、类初始化需要消化非常多的资源，这个资源包括数据、硬件资源等。 <br/>
 *  3、性能和安全要求的场景。<br/>
 *  4、通过 new 产生一个对象需要非常繁琐的数据准备或访问权限，则可以使用原型模式。<br/>
 *  5、一个对象多个修改者的场景。<br/>
 *  6、一个对象需要提供给其他对象访问，而且各个调用者可能都需要修改其值时，可以考虑使用原型模式拷贝多个对象供调用者使用。<br/>
 *  7、在实际项目中，原型模式很少单独出现，一般是和工厂方法模式一起出现，通过 clone 的方法创建一个对象，然后由工厂方法提供给调用者。 <br/>
 * @author shersfy
 * @date 2018-02-05
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class Prototype implements Cloneable{
	
	private Long id;
	private String name;
	private int type;
	private Date createTime;

	// 1. 实现Cloneable接口
	// 2. 覆写Object.clone()方法
	@Override
	protected Object clone(){
		Object clone = null;
		
		try {
			clone = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return clone;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Override
	public String toString() {
		return "Prototype [id=" + id + ", name=" + name + ", type=" + type + ", createTime=" + createTime + "]";
	}

}
