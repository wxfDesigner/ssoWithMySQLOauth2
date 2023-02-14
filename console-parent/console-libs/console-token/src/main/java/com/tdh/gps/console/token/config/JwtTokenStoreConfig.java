package com.tdh.gps.console.token.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

/**
 * 
 * @ClassName: JwtTokenStoreConfig
 * @Description: (jwt服务配置类)
 * @author wxf
 * @date 2018年12月4日 下午3:40:16
 *
 */
@Configuration
//@ConditionalOnProperty(prefix = "sso.security.oauth2", name = "storeType", havingValue = "jwt", matchIfMissing = true)
public class JwtTokenStoreConfig {

//	@Value("${sso.security.jwt.signingKey}")
//	private String signingkey;
	@Value("${sso.security.jwt.certificatePassword}")
	private String certificatePassword;

//    @Bean
//    public TokenEnhancer jwtTokenEnhancer() {
//        return new JwtAccessTokenConverter();
//    }

//	@Bean
//	public TokenStore jetTokenStroe() {
//		return new JwtTokenStore(jwtAccessTokenConverter());
//	}

	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
		final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//		// 设置默认值
//		if (StringUtils.isEmpty(signingkey)) {
//			signingkey = "awbeci";
//		}
//		// 密钥，放到配置文件中
//		jwtAccessTokenConverter.setSigningKey(signingkey);
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("console.jks"),
				certificatePassword.toCharArray());
		jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("console"));
		return jwtAccessTokenConverter;
	}
}
