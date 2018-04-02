package org.shersfy.interview.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
@SpringBootConfiguration
public class InterviewApplication {
	
	@RequestMapping("/hello")
	@ResponseBody
	public String hello(){
		return "hello spring boot";
	}

	public static void main(String[] args) {
		SpringApplication.run(InterviewApplication.class, args);
	}
}
