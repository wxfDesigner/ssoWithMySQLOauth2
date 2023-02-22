package com.tdh.gps.console.zuul.configuration;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.spring.web.SpringfoxWebMvcConfiguration;

//@SpringBootApplication
//@ConditionalOnClass(SpringfoxWebMvcConfiguration.class)
public class WebMvcConfig extends  WebMvcConfigurationSupport{
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");//doc.html是文档的访问路径，可以自己更改
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
