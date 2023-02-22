package com.tdh.gps.console.zuul.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.discovery.ServiceRouteMapper;
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

	@Autowired
	private DiscoveryClient discovery;

	@Autowired
	private ServiceRouteMapper serviceRouteMapper;

	@Autowired
	private ServiceInstance localServiceInstance;

//	@Bean
//	public NewZuulRouteLocator routeLocator() {
//		NewZuulRouteLocator routeLocator = new NewZuulRouteLocator(
////				this.serverProperties.getServlet().getServletPrefix()
//				webMvcProperties.getServlet().getServletPrefix(), this.zuulProperties);
//		return routeLocator;
//	}
	@Bean
	public DiscoveryClientRouteLocator routeLocator() {
//		Assert.notNull(localServiceInstance, "ServiceInstance cannot be empty !");
		DiscoveryClientRouteLocator routeLocator = new DiscoveryClientRouteLocator(
				webMvcProperties.getServlet().getServletPrefix(), discovery, this.zuulProperties, serviceRouteMapper,
				localServiceInstance);
		return routeLocator;
	}
}
