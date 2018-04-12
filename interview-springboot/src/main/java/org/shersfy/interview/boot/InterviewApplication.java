package org.shersfy.interview.boot;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@ComponentScan(basePackages="org.shersfy.interview")
@Controller
public class InterviewApplication {
	
	@Resource
	private SqlSession sqlSession;
	
	@RequestMapping("/hello")
	@ResponseBody
	public String hello(){
		return "hello spring boot";
	}

	public static void main(String[] args) {
		SpringApplication.run(InterviewApplication.class, args);
	}

}
