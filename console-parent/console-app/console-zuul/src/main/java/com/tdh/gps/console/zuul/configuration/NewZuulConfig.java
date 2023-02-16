package com.tdh.gps.console.zuul.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NewZuulConfig {

	@Autowired
	private ZuulProperties zuulProperties;

//	@Autowired
//	private ServerProperties serverProperties;
	@Autowired
	private WebMvcProperties webMvcProperties;

	@Bean
	public NewZuulRouteLocator routeLocator() {
		NewZuulRouteLocator routeLocator = new NewZuulRouteLocator(
//				this.serverProperties.getServlet().getServletPrefix()
				webMvcProperties.getServlet().getServletPrefix(), this.zuulProperties);
		return routeLocator;
	}
}
