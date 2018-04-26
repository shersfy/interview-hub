package org.shersfy.jwatcher.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="org.shersfy.jwatcher")
public class JWatcherMain {

	public static void main(String[] args) {
		SpringApplication.run(JWatcherMain.class, args);
	}

}
