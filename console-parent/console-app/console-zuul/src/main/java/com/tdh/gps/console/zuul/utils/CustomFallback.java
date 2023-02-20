package com.tdh.gps.console.zuul.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.netflix.hystrix.exception.HystrixTimeoutException;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局默认的hystrix熔断服务降级Fallback处理实现类
 * 
 * @author wangxf
 *
 */
@Slf4j
@Component
public class CustomFallback implements FallbackProvider {

	@Override
	public String getRoute() {
		return null;
	}

	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
		log.error(cause.getMessage(), cause);
		// 标记不同的异常为不同的http状态值
		if (cause instanceof HystrixTimeoutException) {
			return response(route, HttpStatus.GATEWAY_TIMEOUT);
		} else {
			// 可继续添加自定义异常类
			return response(route, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 处理
	private ClientHttpResponse response(String route, final HttpStatus status) {
		StringBuilder msg = new StringBuilder();
		msg.append("The ").append(route).append(" server is unavailable !");
		return new ClientHttpResponse() {
			@Override
			public HttpStatus getStatusCode() throws IOException {
				return status;
			}

			@Override
			public int getRawStatusCode() throws IOException {
				return status.value();
			}

			@Override
			public String getStatusText() throws IOException {
				return status.getReasonPhrase();
			}

			@Override
			public void close() {
			}

			@Override
			public InputStream getBody() throws IOException {
				return new ByteArrayInputStream(msg.toString().getBytes());
			}

			@Override
			public HttpHeaders getHeaders() {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);
				return headers;
			}
		};
	}

}
