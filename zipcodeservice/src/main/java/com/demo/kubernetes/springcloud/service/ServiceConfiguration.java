package com.demo.kubernetes.springcloud.service;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.util.logging.Logger;

@Configuration
@ComponentScan
public class ServiceConfiguration {
	protected Logger logger;
	
	public ServiceConfiguration(){
		logger = Logger.getLogger(getClass().getName());
		logger.info("ServiceConfiguration initialized");
	}
}
