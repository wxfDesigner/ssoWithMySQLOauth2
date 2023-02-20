package com.tdh.gps.console.resource.configuration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

/**
 * 
 * @ClassName: CustomTokenExtractor
 * @Description: (自定义的Token提取器)
 * @author wxf
 * @date 2018年12月19日 上午9:57:10
 *
 */
@Component("customTokenExtractor")
public class CustomTokenExtractor extends BearerTokenExtractor {

	private final Logger logger = LoggerFactory.getLogger(CustomTokenExtractor.class);

	@Override
	protected String extractToken(HttpServletRequest request) {
		// first check the header...
		String token = extractHeaderToken(request);

		// bearer type allows a request parameter as well
		if (null == token) {
			logger.debug("Token not found in headers. Trying request parameters.");
			token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
			if (null == token) {
				logger.debug("Token not found in request parameters. Trying request cookies.");
				Cookie[] cookies = request.getCookies();
				if (null != cookies) {
					for (Cookie cookie : cookies) {
						if (OAuth2AccessToken.ACCESS_TOKEN.equals(cookie.getName())) {
							token = cookie.getValue();
							break;
						}
					}
				}
				if (null == token) {
					logger.debug("Token not found in request cookies.  Not an OAuth2 request.");
				}
			}
		}
		if (null != token) {
			request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
		}
		return token;
	}

}
