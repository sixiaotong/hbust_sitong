package com.hbust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class SitongWebmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SitongWebmanagementApplication.class, args);
	}

}
