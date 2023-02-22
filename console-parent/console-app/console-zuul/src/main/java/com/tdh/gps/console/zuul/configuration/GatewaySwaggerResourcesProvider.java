package com.tdh.gps.console.zuul.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
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
@Component
@Primary
//@EnableOpenApi
public class GatewaySwaggerResourcesProvider implements SwaggerResourcesProvider {
    private final RouteLocator routeLocator;
    public GatewaySwaggerResourcesProvider(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<Route> routes = routeLocator.getRoutes();
        for (Route route:routes) {
        	resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v3/api-docs"),"3.0"));
        }
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
