package com.tdh.gps.console.zuul.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

/**
 * swagger Api接口文档配置类
 * 
 * @author wangxf
 *
 */
//@Component
//@Primary
class SwaggerApiDocConfiguration implements SwaggerResourcesProvider {
	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<SwaggerResource>();
		resources.add(swaggerResource("console-web", "/console-web/v3/api-docs", "3.0"));
		resources.add(swaggerResource("console-authentication", "/console-authentication/v3/api-docs", "3.0"));
		return resources;
	}

	private SwaggerResource swaggerResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}
}