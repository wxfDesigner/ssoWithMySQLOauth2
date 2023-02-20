package com.tdh.gps.console.zuul.filter;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.netflix.zuul.context.RequestContext;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局默认的异常处理过滤器
 * 
 * @author wangxf
 *
 */
@Slf4j
@Component
public class CustomErrorFilter extends SendErrorFilter {

	@Override
	public Object run() {
		StringBuilder msg = new StringBuilder("Failed to requested ! ");
		PrintWriter pw = null;
		try {
			RequestContext ctx = RequestContext.getCurrentContext();
			ExceptionHolder exception = findZuulException(ctx.getThrowable());
			log.error(exception.getErrorCause(), exception.getThrowable());
			msg.append("error message:").append(exception.getErrorCause());
			HttpServletResponse response = ctx.getResponse();
			response.setCharacterEncoding("UTF-8");
			pw = response.getWriter();
			pw.write(msg.toString());
			pw.flush();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			ReflectionUtils.rethrowRuntimeException(ex);
		} finally {
			if (null != pw) {
				pw.close();
			}
		}
		return msg.toString();
	}

}
