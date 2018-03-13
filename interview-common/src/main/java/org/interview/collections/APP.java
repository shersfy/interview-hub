package org.interview.collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 常用集合类
 * @author shersfy
 * @date 2018-03-12
 *
 * @copyright Copyright shersfy 2018 All Rights Reserved.
 */
public class APP {

	public static void main( String[] args ) {
		/**
		 * 集合接口关系，见类图
		 * 
		 */
		APP app = new APP();
		// Collection
		// 非线程安全异常 ConcurrentModificationException
		// 1.List
		// ArrayList 非线程安全。采用线性(数组)连续存储空间，当存储空间不足的时候，ArrayList默认增加为原来的1/2倍。适合频繁读取get/set
		System.out.println("======ArrayList============");
		app.arrayList();
		// Vector 线程安全。采用线性连续存储空间，当存储空间不足的时候，Vector默认增加为原来的1倍，性能低
		System.out.println("======Vector============");
		app.vector();
		// Stack 线程安全。继承Vector， 实现了元素先进后出FILO的pop()方法。采用线性连续存储空间，当存储空间不足的时候，Vector默认增加为原来的1倍
		System.out.println("======Stack============");
		app.stack();
		// LinkedList 非线程安全。双向链表, 链式存储(链表)， 还实现了Queue接口, 先进线程队列。适用于频繁插入add/remove
		System.out.println("======LinkedList============");
		app.linkedList();
		// CopyOnWriteArrayList 兼顾了线程安全的同时，又提高了并发性，性能比Vector有不少提高
		System.out.println("======CopyOnWriteArrayList============");
		app.copyOnWriteArrayList();

		// 2.Queue
		// LinkedList 非线程安全。链表存储
		System.out.println("======LinkedList============");
		app.linkedList();
		// ArrayDeque 非线程安全。数组存储
		System.out.println("======ArrayDeque============");
		app.arrayDeque();

		// 3.Set
		// HashSet 非线程安全。元素散列存储，不能保证元素的排列顺序，遍历无序, 顺序有可能发生变化, get/set性能较好
		System.out.println("======HashSet============");
		app.hashSet();
		// LinkedHashSet 继承HashSet，非线程安全。元素链式存储, 遍历有序,  add/remove性能较好
		System.out.println("======LinkedHashSet============");
		app.linkedHashSet();
		// TreeSet 非线程安全。元素树存储, 遍历有序，因为内部使用了TreeMap， 而TreeMap key不能为null，所以不能添加null元素
		System.out.println("======TreeSet============");
		app.treeSet();
		// CopyOnWriteArraySet 线程安全
		System.out.println("======CopyOnWriteArraySet============");
		app.copyOnWriteArraySet();


		// 4.Map
		// HashMap 非线程安全。元素散列存储，不能保证元素的排列顺序，遍历无序, 顺序有可能发生变化, key可以为null
		System.out.println("======HashMap============");
		app.hashMap();
		// LinkedHashMap 继承HashMap, 非线程安全。元素链式存储, 遍历有序,  add/remove性能较好
		System.out.println("======LinkedHashMap============");
		app.linkedHashMap();
		// TreeMap 非线程安全。元素树存储, 遍历有序(键排序，默认键升序)，key不能为null，所以不能添加null元素, 为null报NullPointerException
		System.out.println("======TreeMap============");
		app.treeMap();
		// Hashtable 继承抽象类Dictionary， 同时实现Map接口。 线程安全， 不能保证元素的排列顺序，遍历无序, 顺序有可能发生变化, key 和 value都不能为null
		System.out.println("======Hashtable============");
		app.hashtable();

		// Properties 继承Hashtable, 线程安全， 不能保证元素的排列顺序，遍历无序, 顺序有可能发生变化, key 和 value都不能为null
		System.out.println("======Properties============");
		app.properties();
		
		// ConcurrentHashMap 直接实现ConcurrentMap接口， 间接实现Map接口, 线程安全， 不能保证元素的排列顺序，遍历无序, 顺序有可能发生变化, key 和 value都不能为null
		System.out.println("======ConcurrentHashMap============");
		app.concurrentHashMap();
		
		// 5. Collections类可以产生一些特殊的集合类，如线程安全的list set map，以及空集合
		System.out.println("======Empty Collections============");
		System.out.println(String.format("%s, %s", 
				Collections.emptyList().getClass().getName(), 
				Collections.emptyList()!=null));
		System.out.println(String.format("%s, %s", 
				Collections.emptySet().getClass().getName(), 
				Collections.emptySet()!=null));
		System.out.println(String.format("%s, %s", 
				Collections.emptyMap().getClass().getName(), 
				Collections.emptySet()!=null));
		
		// 总结
		// list set map区别
		// list元素可以重复, set元素不重复, 自动去重,  map键值对, key重复value被覆盖, key不能为null 
		
		
	}


	public void arrayList() {
		List<String> alist = new ArrayList<>();
		alist.add("element1");
		alist.add("element2");
		alist.add("element2");
		alist.add("element3");
		alist.add(null);
		for(String e :alist) {
			System.out.println(e);
		}
	}

	public void linkedList() {
		LinkedList<String> alist = new LinkedList<>();
		alist.add("element1");
		alist.add("element2");
		alist.add("element2");
		alist.add("element3");
		for(String e :alist) {
			System.out.println(e);
		}

		while(alist.iterator().hasNext()) {
			System.out.println(String.format("FIFO, size=%s, value=%s", alist.size(), alist.poll()));
		}
	}

	public void arrayDeque() {
		Queue<String> alist = new ArrayDeque<>();
		alist.add("element1");
		alist.add("element2");
		alist.add("element2");
		alist.add("element3");

		for(String e :alist) {
			System.out.println(e);
		}
		while(alist.iterator().hasNext()) {
			System.out.println(String.format("FIFO, size=%s, value=%s", alist.size(), alist.poll()));
		}
	}

	public void stack() {
		Stack<String> stack = new Stack<>();
		stack.add("element1");
		stack.add("element2");
		stack.add("element2");
		stack.add("element3");

		for(String e :stack) {
			System.out.println(e);
		}
		while(stack.iterator().hasNext()) {
			System.out.println(String.format("LIFO, size=%s, value=%s", stack.size(), stack.pop()));
		}
	}

	public void vector() {
		List<String> alist = new Vector<>();
		alist.add("element1");
		alist.add("element2");
		alist.add("element2");
		alist.add("element3");
		for(String e :alist) {
			System.out.println(e);
		}
	}


	public void copyOnWriteArrayList() {
		List<String> alist = new CopyOnWriteArrayList<>();
		alist.add("element1");
		alist.add("element2");
		alist.add("element2");
		alist.add("element3");
		for(String e :alist) {
			System.out.println(e);
		}
	}

	public void hashSet() {
		Set<String> set = new HashSet<>();
		set.add("element1");
		set.add("element2");
		set.add(null);
		set.add("element2");
		set.add("element3");
		for(String e :set) {
			System.out.println(e);
		}
	}

	public void linkedHashSet() {
		Set<String> set = new LinkedHashSet<>();
		set.add("element1");
		set.add("element2");
		set.add(null);
		set.add("element2");
		set.add("element3");
		for(String e :set) {
			System.out.println(e);
		}
	}

	public void treeSet() {
		Set<String> set = new TreeSet<>();
		set.add("element1");
		set.add("element2");
		set.add("element2");
		//    	set.add(null);
		set.add("element3");
		for(String e :set) {
			System.out.println(e);
		}
	}

	public void copyOnWriteArraySet() {
		Set<String> set = new CopyOnWriteArraySet<>();
		set.add("element1");
		set.add("element2");
		set.add("element2");
		set.add(null);
		set.add("element3");
		for(String e :set) {
			System.out.println(e);
		}
	}

	public void hashMap() {

		Map<String, Object> map = new HashMap<>();
		map.put("key1", "value1");
		map.put(null, null);
		map.put("key3", "value3");
		map.put("key2", "value2");
		map.put("key2", "value2");

		for(String key :map.keySet()) {
			System.out.println(String.format("%s-->%s", key, map.get(key)));
		}
	}

	public void linkedHashMap() {

		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key1", "value1");
		map.put(null, null);
		map.put("key3", "value3");
		map.put("key2", "value2");
		map.put("key2", "value2");

		for(String key :map.keySet()) {
			System.out.println(String.format("%s-->%s", key, map.get(key)));
		}
	}

	public void treeMap() {

		Map<String, Object> map = new TreeMap<>();
		map.put("key1", "value1");
		map.put("key3", "value3");
		map.put(" ", null);
		map.put("key2", "value2");
		map.put("key2", "value2");

		for(String key :map.keySet()) {
			System.out.println(String.format("%s-->%s", key, map.get(key)));
		}
	}

	public void hashtable() {

		Map<String, Object> map = new Hashtable<>();
		map.put("key1", "value1");
		//    	map.put(" ", null);
		map.put("key3", "value3");
		map.put("key2", "value2");
		map.put("key2", "value2");

		for(String key :map.keySet()) {
			System.out.println(String.format("%s-->%s", key, map.get(key)));
		}
	}
	
	public void properties() {
		
		Map<Object, Object> map = new Properties();
		map.put("key1", "value1");
//		map.put(" ", null);
		map.put("key3", "value3");
		map.put("key2", "value2");
		map.put("key2", "value2");
		
		for(Object key :map.keySet()) {
			System.out.println(String.format("%s-->%s", key, map.get(key)));
		}
	}
	
	public void concurrentHashMap() {
		
		Map<Object, Object> map = new ConcurrentHashMap<>();
		map.put("key1", "value1");
//		map.put(" ", null);
		map.put("key3", "value3");
		map.put("key2", "value2");
		map.put("key2", "value2");
		
		for(Object key :map.keySet()) {
			System.out.println(String.format("%s-->%s", key, map.get(key)));
		}
	}

}
