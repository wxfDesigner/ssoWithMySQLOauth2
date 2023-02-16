package com.tdh.gps.console.token.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.util.Assert;

/**
 * 
 * @ClassName: SsoAuthorizationServerConfigurer
 * @Description: (sso认证服务配置类)
 * @author wxf
 * @date 2018年12月4日 下午3:42:15
 *
 */
@Configuration
@EnableAuthorizationServer
public class SsoAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {
	private static String REALM = "MY_OAUTH_REALM";

	@Autowired(required = false)
	private JwtAccessTokenConverter jwtAccessTokenConverter;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

//	MongoDB DataSource begin

//	@Autowired
//	private TokenStore tokenStore;
//	
//	@Autowired
//	private UserApprovalHandler userApprovalHandler;
//
//	@Autowired
//	@Qualifier("mongoAuthorizationCodeServices")
//	private AuthorizationCodeServices authorizationCodeServices;
//
//	@Autowired
//	@Qualifier("mongoClientDetailsService")
//	private ClientDetailsService clientDetailsService;

//	MongoDB DataSource end

//	MySQL DataSource begin

	@Autowired
	private DataSource dataSource;

	@Autowired
	private RedisConnectionFactory redisConnectionFactory;
	
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Bean
	public TokenStore tokenStore() {
		return new RedisTokenStore(redisConnectionFactory);
	}

//	@Bean("jdbcClientDetailsService")
//	public ClientDetailsService clientDetailsService() {
//		return new JdbcClientDetailsService(dataSource);
//	}

	@Bean
	public AuthorizationCodeServices authorizationCodeServices() {
		return new JdbcAuthorizationCodeServices(dataSource);
	}

	@Bean
	public UserApprovalHandler userApprovalHandler() {
		Assert.notNull(clientDetailsService, "ClientDetailsService cannot be empty !");
//		ClientDetailsService clientDetailsService = clientDetailsService();
		TokenStoreUserApprovalHandler userApprovalHandler = new TokenStoreUserApprovalHandler();
		userApprovalHandler.setTokenStore(tokenStore());
		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
		userApprovalHandler.setClientDetailsService(clientDetailsService);
		return userApprovalHandler;
	}

//	MySQL DataSource end

//	@Autowired(required = false)
//	private TokenEnhancer jwtTokenEnhancer;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource);
//		clients.withClientDetails(clientDetailsService());
//		clients.inMemory().withClient("my-trusted-client")// 客户端ID
//				.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
//				.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust")// 授权用户的操作权限
//				.secret(passwordEncoder().encode("secret"))// 密码
//				.accessTokenValiditySeconds(1800).refreshTokenValiditySeconds(3000)// token有效期为1800秒
//				.resourceIds("console_web_rest_api", "my_rest_api");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore()).userApprovalHandler(userApprovalHandler())
				.authenticationManager(authenticationManager);
		// 配置TokenServices参数
//		DefaultTokenServices tokenServices = new DefaultTokenServices();
//		tokenServices.setTokenStore(endpoints.getTokenStore());
//		tokenServices.setSupportRefreshToken(false);
//		tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
//		tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
//		tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(1)); // 1天
//		endpoints.tokenServices(tokenServices);
		if (jwtAccessTokenConverter != null) {
			TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
			List<TokenEnhancer> enhancers = new ArrayList<TokenEnhancer>();
//            enhancers.add(jwtTokenEnhancer);
			enhancers.add(jwtAccessTokenConverter);
			enhancerChain.setTokenEnhancers(enhancers);
			endpoints.tokenEnhancer(enhancerChain).accessTokenConverter(jwtAccessTokenConverter);
		}
		endpoints.authorizationCodeServices(authorizationCodeServices());

	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.realm(REALM + "/client").tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");

	}

}
