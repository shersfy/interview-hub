package org.shersfy.interview.service;

import org.springframework.stereotype.Component;

@Component
public class UserService {
	
	public boolean login(String username, String pwd){
		
		try {
			Thread.sleep(10*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

}
