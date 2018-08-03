package com.demo.kubernetes.springcloud.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceController {
	private static final String URLPrefix = "https://www.zipcodeapi.com/rest/REDACTED/";
	private static final String GET = "GET";
	
	protected Logger logger = Logger.getLogger(ServiceController.class.getName());

	@Autowired
	public ServiceController() {
		logger.info("ServiceController initiated");
	}

	@RequestMapping(value = "/zipcodeservice/info/{zipcode}", produces = { "application/json" })
	public String getZipcodeInfo(@PathVariable("zipcode") Integer zipcode) {
		return getURLResponse("info.json/" + zipcode + "/degrees");
	}

	@RequestMapping(value = "/zipcodeservice/nearby/{zipcode}/{distance}", produces = { "application/json" })
	public String getNearbyZipcodes(@PathVariable("zipcode") Integer zipcode,
			@PathVariable("distance") Integer distance) {
		return getURLResponse("radius.json/" + zipcode + "/" + distance + "/mile");
	}

	private String getURLResponse(String path) {
		try {
			URL url = new URL(URLPrefix + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(GET);

			StringBuilder result = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			return result.toString();
		} catch (MalformedURLException e1) {
			logger.log(Level.SEVERE, e1.getMessage(), e1);
			return null;

		} catch (IOException e2) {
			logger.log(Level.SEVERE, e2.getMessage(), e2);
			return null;
		}
	}
}
