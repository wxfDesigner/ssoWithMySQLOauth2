package com.tdh.gps.console.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @ClassName: Application
 * @Description: (Token令牌授权认证服务启动类)
 * @author wxf
 * @date 2018年12月6日 上午10:13:27
 *
 */
@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class })
@ComponentScan(basePackages = { "com.tdh.gps.console.*" })
public class Application {
//	static Logger logger = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		log.info("console authentication started successfully");
	}

}
