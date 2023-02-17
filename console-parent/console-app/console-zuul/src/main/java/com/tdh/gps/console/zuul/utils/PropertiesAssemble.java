package com.tdh.gps.console.zuul.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.tdh.gps.console.zuul.model.ZuulRouteEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PropertiesAssemble {

	@Value("${spring.cloud.nacos.config.server-addr}")
	private String serverAddr;
	@Value("${spring.cloud.nacos.config.namespace}")
	private String namespace;

	public Map<String, ZuulRoute> getProperties() {
		Map<String, ZuulRoute> routes = new LinkedHashMap<>();
		List<ZuulRouteEntity> results = listenerNacos("zuul-server", "zuul_route");
		for (ZuulRouteEntity result : results) {
			if (StringUtils.isBlank(result.getPath())
			/* || org.apache.commons.lang3.StringUtils.isBlank(result.getUrl()) */) {
				continue;
			}
			ZuulRoute zuulRoute = new ZuulRoute();
			try {
				BeanUtils.copyProperties(result, zuulRoute);
			} catch (Exception e) {
			}
			routes.put(zuulRoute.getPath(), zuulRoute);
		}
		return routes;
	}

	private List<ZuulRouteEntity> listenerNacos(String dataId, String group) {
		try {
			Properties properties = new Properties();
			properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
			properties.put(PropertyKeyConst.NAMESPACE, namespace);
			ConfigService configService = NacosFactory.createConfigService(properties);
			String content = configService.getConfig(dataId, group, 5000);
			log.info("从Nacos返回的配置：{}", content);
			// 注册Nacos配置更新监听器
//            configService.addListener(dataId, group, new Listener()  {
//                @Override
//                public void receiveConfigInfo(String configInfo) {
//                    System.out.println("Nacos更新了！");
//
//                }
//                @Override
//                public Executor getExecutor() {
//                    return null;
//                }
//            });
			return JSONObject.parseArray(content, ZuulRouteEntity.class);
		} catch (NacosException e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}