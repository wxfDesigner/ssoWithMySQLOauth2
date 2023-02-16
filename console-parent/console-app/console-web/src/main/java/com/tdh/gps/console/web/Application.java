package com.tdh.gps.console.web;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: Application
 * @Description: (Web服务启动类)
 * @author wxf
 * @date 2018年12月10日 下午3:34:53
 *
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class })
@ComponentScan(basePackages = { "com.tdh.gps.console.*" })
public class Application {
//	static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class).web(WebApplicationType.SERVLET).run(args);
		log.info("console web started successfully");
	}
}
