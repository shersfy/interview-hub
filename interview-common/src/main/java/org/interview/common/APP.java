package org.interview.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
/**
 */
public class APP extends Thread{
	
	private volatile Keywords words;
	private AtomicInteger var;
	
	APP(Keywords words, AtomicInteger var, String name){
		this.words = words;
		this.var = var;
		this.setName(name);
	}
	
    public static void main( String[] args ) throws InterruptedException
    {
    	Keywords kw = new Keywords("Jim", 0, "ZH");
    	AtomicInteger var = new AtomicInteger(0);
    	
    	CountDownLatch latch = new CountDownLatch(1);
    	
    	new APP(kw, var, "A").start();
    	new APP(kw, var, "B").start();
    	new APP(kw, var, "C").start();
    	
    	latch.await();
    	
    }
    

	@Override
	public void run() {
		try {
			while(true) {
				Thread.sleep(1000);
//				synchronized (words) {
					words.setAge(words.getAge()+1);
//				}
				var.getAndIncrement();
				System.out.println(String.format("%s: kw=%s, var=%s, age%svar", this.getName(), words, var, var.get()==words.getAge()?"=":"!=")); // 11
			}
		} catch (Exception e) {
		}
	}

	public Keywords getWords() {
		return words;
	}

	public void setWords(Keywords words) {
		this.words = words;
	}

	public AtomicInteger getVar() {
		return var;
	}

	public void setVar(AtomicInteger var) {
		this.var = var;
	}



}
