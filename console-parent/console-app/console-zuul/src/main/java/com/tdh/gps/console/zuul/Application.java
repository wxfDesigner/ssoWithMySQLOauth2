package com.tdh.gps.console.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

/**
 * zuul 网关服务启动器类
 * @author wangxf
 *
 */
@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@ComponentScan(basePackages = { "com.tdh.gps.console.*" })
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("console-zuul started successfully !");
    }
}
