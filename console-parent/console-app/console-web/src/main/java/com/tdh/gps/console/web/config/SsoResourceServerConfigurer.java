package com.tdh.gps.console.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 
 * @ClassName: SsoWebSecurityConfigurer
 * @Description: (sso资源服务配置类)
 * @author wxf
 * @date 2018年12月4日 下午3:43:56
 *
 */
@Configuration
@EnableResourceServer
public class SsoResourceServerConfigurer extends ResourceServerConfigurerAdapter {

	private static final String RESOURCE_ID = "console_web_rest_api";
//	@Value("${security.oauth2.client.clientId}")
//	private String clientId;
//	@Value("${security.oauth2.client.clientSecret}")
//	private String clientSecret;
//	@Value("${security.oauth2.authorization.check-token-access}")
//	private String checkTokenEndpointUrl;
	
//	MongoDB DataSource begin
	
//	@Autowired
//	private TokenStore tokenStore;
	
//	MongoDB DataSource end
	
//	Redis DataSource begin
	
	@Autowired
	private RedisConnectionFactory redisConnectionFactory;

	@Bean
	public TokenStore tokenStore() {
		return new RedisTokenStore(redisConnectionFactory);
	}
	
//  Redis DataSource end
	
	@Autowired(required = false)
	private JwtAccessTokenConverter jwtAccessTokenConverter;
	@Autowired
	@Qualifier("customTokenExtractor")
	private TokenExtractor tokenExtractor;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID).stateless(false).tokenServices(tokenServices()).tokenStore(tokenStore())
				.tokenExtractor(tokenExtractor);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable().requestMatchers().antMatchers("/console*/**").and().authorizeRequests()
				.antMatchers("/console*/**").permitAll().and().exceptionHandling()
				.accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}

	@Bean
	public ResourceServerTokenServices tokenServices() {
//		RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
//		remoteTokenServices.setCheckTokenEndpointUrl(checkTokenEndpointUrl);
//		remoteTokenServices.setClientId(clientId);
//		remoteTokenServices.setClientSecret(clientSecret);
//		remoteTokenServices.setAccessTokenConverter(accessTokenConverter());
//		return remoteTokenServices;
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setTokenStore(tokenStore());
		tokenServices.setSupportRefreshToken(true);
//		tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
		tokenServices.setTokenEnhancer(jwtAccessTokenConverter);
		tokenServices.setAccessTokenValiditySeconds(1800); // 1800秒
		tokenServices.setRefreshTokenValiditySeconds(3000);
		return tokenServices;

	}

	@Bean
	public AccessTokenConverter accessTokenConverter() {
		return new DefaultAccessTokenConverter();
	}

}
