package com.appspdeveloperblogapp.ws;

import com.appspdeveloperblogapp.ws.security.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MobileAppWsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MobileAppWsApplication.class, args);
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
	public SpringApplicaitonContext springApplicaitonContext(){
		return new SpringApplicaitonContext();
	}

	@Bean(name="AppProperties")
	public AppProperties getAppProperties(){
		return new AppProperties();
	}
}
