package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;


/**
 * This class is not used. Can be configured and set up on any integration test (@ContextConfiguration(classes = { TestDemoApplication.class }))
 */
//@SpringBootApplication(scanBasePackages = { "com.example.demo" })
//@Import({ TestcontainersConfiguration.class })
//@ConfigurationPropertiesScan("com.example.demo")
public class TestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.from(DemoApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

//	public static void main(String[] args) {
//		SpringApplication.run(TestDemoApplication.class, args);
//	}

}
