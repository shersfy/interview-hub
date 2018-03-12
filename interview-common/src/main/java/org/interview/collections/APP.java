package org.interview.collections;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
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
    	// 1.List
    	System.out.println("======ArrayList============");
    	app.arrayList();
    	System.out.println("======Vector============");
    	app.vector();
    	System.out.println("======Stack============");
    	app.stack();
    	System.out.println("======CopyOnWriteArrayList============");
    	app.copyOnWriteArrayList();
    	System.out.println("======LinkedList============");
    	
    	// 2.Queue
    	app.linkedList();
    	System.out.println("======ArrayDeque============");
    	app.arrayDeque();
    	
    	System.out.println("======HashSet============");
    	// 3.Set
    	app.hashSet();
    	System.out.println("======CopyOnWriteArraySet============");
    	app.copyOnWriteArraySet();
    	
    	
    	// 4.Map
    }
    
    
    public void arrayList() {
    	List<String> alist = new ArrayList<>();
    	alist.add("element1");
    	alist.add("element2");
    	alist.add("element2");
    	alist.add("element3");
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
    	set.add("element2");
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
    	set.add("element3");
    	for(String e :set) {
    		System.out.println(e);
    	}
    }
}
