package com.tdh.gps.console.resource.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import com.tdh.gps.console.common.constants.ResourceServerConstants;

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

	@Value("${spring.application.name}")
	private String resourceId;
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
		resources.resourceId(resourceId+ResourceServerConstants.RESOURCE_ID_SUFFIX).stateless(false).tokenServices(tokenServices()).tokenStore(tokenStore())
				.tokenExtractor(tokenExtractor);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.addFilterAfter(dynamicallyUrlInterceptor(), FilterSecurityInterceptor.class) //添加url认证filter
				.authorizeRequests().antMatchers(
				"/swagger-resources/**" //swagger需要的静态资源路径
                ,"/**/v3/**"
                ,"/swagger-ui/**"
                ,"/**/oauth/token").permitAll()
				.and().authorizeRequests().anyRequest().authenticated()
				.and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler())
				.and().csrf().disable();// 关闭csrf
//		http.anonymous().disable().requestMatchers().antMatchers("/console*/**").and().authorizeRequests()
//		.antMatchers("/console*/**").permitAll().and().exceptionHandling()
//		.accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}
	
	 /*
     * 创建我的自定义的url的interceptor
     *
     */
    @Bean
    public DynamicallyUrlInterceptor dynamicallyUrlInterceptor(){
        DynamicallyUrlInterceptor interceptor = new DynamicallyUrlInterceptor();
        interceptor.setSecurityMetadataSource(new CustomFilterSecurityMetadataSource());

        //配置RoleVoter决策
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<AccessDecisionVoter<? extends Object>>();
        decisionVoters.add(new RoleVoter());
        //设置认证决策管理器
        interceptor.setAccessDecisionManager(new DynamicallyUrlAccessDecisionManager(decisionVoters));
        return interceptor;
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

//	@Bean
//	public AccessTokenConverter accessTokenConverter() {
//		return new DefaultAccessTokenConverter();
//	}

}
