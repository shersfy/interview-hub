package org.interview.multithread;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MultiThread extends Thread {

	private List<String> container;
	private CountDownLatch latch;
	
	public MultiThread(String name, CountDownLatch latch, List<String> container) {
		super.setName(name);
		this.latch = latch;
		this.container = container;
	}
	
	
	public void addElment() throws InterruptedException {
		synchronized (container) {
			System.out.println(String.format("thread %s executing...", getName()));
			for(int i=0; i<5; i++) {
				String e = "element"+i;
				container.add(e);
				System.out.println(String.format("thread %s added %s, container size=%s", 
						getName(), e, container.size()));
				if(container.size()==3) {
					System.out.println(String.format("thread %s wait release lock before, container size=%s ...", 
							getName(), container.size()));
					container.wait();
					System.out.println(String.format("thread %s aware released lock after, container size=%s", 
							getName(), container.size()));
				}
				if(container.size()==5) {
					System.out.println(String.format("thread %s notify another thread before, container size=%s ...", 
							getName(), container.size()));
					container.notify();
					System.out.println(String.format("thread %s notified another thread after, container size=%s", 
							 getName(), container.size()));
					break;
				}
				sleep(2000);
			}
			System.out.println(String.format("thread %s executed", getName()));
		}
	}

	public List<String> getContainer() {
		return container;
	}

	public void setContainer(List<String> container) {
		this.container = container;
	}


	@Override
	public void run() {
		try {
			addElment();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		latch.countDown();
	}
	
}
