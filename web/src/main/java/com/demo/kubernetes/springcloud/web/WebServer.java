package com.demo.kubernetes.springcloud.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(useDefaultFilters = false)
public class WebServer {
	
	public static String web_service_url = null;
	
	public static void main(String[] args) {
		if(args.length != 1 && args.length != 2){
			errorMessage();
			return;
		}else if(args.length == 2){
			web_service_url = args[0];
			System.setProperty("server.port", args[1]);
		}else if(args.length == 1){
			web_service_url = args[0];
		}
		System.setProperty("spring.config.name", "web-server");
		SpringApplication.run(WebServer.class, args);
	}
	
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	public WebService service() {
		return new WebService(web_service_url);
	}
	
	@Bean
	public WebController patientController() {
		return new WebController(service());
	}
	
	protected static void errorMessage() {
		System.out.println("Usage: java -jar [jar file name] <zip web service url> <port> OR");
		System.out.println("Usage: java -jar [jar file name] <zip web service url>");
	}
}
