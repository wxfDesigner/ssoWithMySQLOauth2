package com.tdh.gps.console.resource.configuration;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

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
	private Logger logger = LoggerFactory.getLogger(JwtTokenStoreConfig.class);
//	@Value("${sso.security.jwt.signingKey}")
//	private String signingkey;

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
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
//		// 设置默认值
//		if (StringUtils.isEmpty(signingkey)) {
//			signingkey = "awbeci";
//		}
//		// 密钥，放到配置文件中
//		jwtAccessTokenConverter.setSigningKey(signingkey);
		Resource resource = new ClassPathResource("pub.key");
		InputStream inputStream = null;
		StringBuilder publicKey = new StringBuilder();
		try {
			inputStream = resource.getInputStream();
			byte[] bytes = new byte[1024];
			while (inputStream.read(bytes) > 0) {
				publicKey.append(new String(bytes));
			}
		} catch (IOException e) {
			logger.error("fail to load public key", e);
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					logger.error("fail to load public key", e);
				}
			}
		}
//		logger.info("public key:" + publicKey.toString());
		jwtAccessTokenConverter.setVerifierKey(publicKey.toString());
		return jwtAccessTokenConverter;
	}
}
