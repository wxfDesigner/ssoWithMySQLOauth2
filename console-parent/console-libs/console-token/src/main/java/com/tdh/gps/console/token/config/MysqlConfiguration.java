//package com.tdh.gps.console.token.config;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
//import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
//import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
//import org.springframework.util.Assert;
//
///**
// * MySQL数据源的security oauth2初始化配置类
// * 
// * @author wangxf
// *
// */
//@Configuration
//public class MysqlConfiguration {
//
//	@Bean("tokenStore")
//	@Autowired
//	public TokenStore tokenStore(DataSource dataSource) {
//		return new JdbcTokenStore(dataSource);
//	}
//
//	@Bean("clientDetailsService")
//	@Autowired
//	public ClientDetailsService clientDetailsService(DataSource dataSource) {
//		return new JdbcClientDetailsService(dataSource);
//	}
//
//	@Bean("authorizationCodeServices")
//	@Autowired
//	public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
//		return new JdbcAuthorizationCodeServices(dataSource);
//	}
//
//	@Bean("userApprovalHandler")
//	@Autowired
//	@DependsOn({ "tokenStore", "clientDetailsService" })
//	public UserApprovalHandler userApprovalHandler(TokenStore tokenStore, ClientDetailsService clientDetailsService) {
//		Assert.notNull(tokenStore, "TokenStore cannot be empty !");
//		Assert.notNull(clientDetailsService, "ClientDetailsService cannot be empty !");
//		TokenStoreUserApprovalHandler userApprovalHandler = new TokenStoreUserApprovalHandler();
//		userApprovalHandler.setTokenStore(tokenStore);
//		userApprovalHandler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
//		userApprovalHandler.setClientDetailsService(clientDetailsService);
//		return userApprovalHandler;
//	}
//
//}
