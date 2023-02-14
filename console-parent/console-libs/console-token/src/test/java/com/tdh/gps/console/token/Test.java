package com.tdh.gps.console.token;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

public class Test {

	public static void main(String[] args) {
//		System.out.println(extractTokenKey("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NzYyODQ2MDMsInVzZXJfbmFtZSI6ImJvYiIsImp0aSI6IjQ2MTkwNjc2LTgxNzAtNDQ4MC1hN2Q5LTgxZTVkYzE1NDEyMyIsImNsaWVudF9pZCI6Im15LXRydXN0ZWQtY2xpZW50Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIiwidHJ1c3QiXX0.Bq5_BgcadCQ1nQNoFezUHCruzH4MAp_1KZXga4uw0P6JndAvKtBBSbT7IYw-8vDV77MWFel648bSt_vtMltMJ5OGM3fQ-eJm2CyvjvgyQLar-0VgsoHo0mIBYVj0L67TS8xzlNh75sC3XXwrbK4KgMBD049rrmU2T_HXk3OUUy6lhsvZouQUoFbxsu00cGjoFJkMUoN2G5k0qlhVrRTnK8soLjsR3e7pomVVwUgOd1zIn-sMHRHE0SiN2zUQJsoktQkeE5wIOuihvElwOyVUQfEtwb9FK-b0_-i2H-rtxBdP8KJyznJRjurM4qMMKhC8WTK7bHEWk_YPv0sZYVKVcw, token_type=bearer, refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NzYyODU4MDMsInVzZXJfbmFtZSI6ImJvYiIsImp0aSI6ImQwMWVhM2U1LWFmMjItNDk0NC05ZTM2LWJkMDZhODhlNGVlOCIsImNsaWVudF9pZCI6Im15LXRydXN0ZWQtY2xpZW50Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIiwidHJ1c3QiXSwiYXRpIjoiNDYxOTA2NzYtODE3MC00NDgwLWE3ZDktODFlNWRjMTU0MTIzIn0.buBD6yxJpI7JFY6LKw9X_3Ice-m5o0gFNIe4w-yd4izWvZx5Zsq9gOsHbBAYlmdy0PVH2-tybKn9cB3LPFkMFn0feujA6h-vpb897qEHkWH9uXs5UPX24Ky9wNOCTshPEoZVN2afZ__8c_a9SsI0aHlxSoDKfLeCgzsdlrePFgULl2-IDTB9rHT80fb8rAQiuxiDF7F1dYorBX5Ag9WxNAnE0tf4YZQjJxapVWZ1fTyZ6rbLNqwqyHep98vr_hySVNJqO2O8XyWVPOwnnNaBIPUDumsZOl_qtC41dRfrvlgiD9lQHbrN3mp2YEAS2NOR9kp5e8DuG3DPLTwea2LRfQ"));

		String value="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NzYyODQ2MDMsInVzZXJfbmFtZSI6ImJvYiIsImp0aSI6IjQ2MTkwNjc2LTgxNzAtNDQ4MC1hN2Q5LTgxZTVkYzE1NDEyMyIsImNsaWVudF9pZCI6Im15LXRydXN0ZWQtY2xpZW50Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIiwidHJ1c3QiXX0.Bq5_BgcadCQ1nQNoFezUHCruzH4MAp_1KZXga4uw0P6JndAvKtBBSbT7IYw-8vDV77MWFel648bSt_vtMltMJ5OGM3fQ-eJm2CyvjvgyQLar-0VgsoHo0mIBYVj0L67TS8xzlNh75sC3XXwrbK4KgMBD049rrmU2T_HXk3OUUy6lhsvZouQUoFbxsu00cGjoFJkMUoN2G5k0qlhVrRTnK8soLjsR3e7pomVVwUgOd1zIn-sMHRHE0SiN2zUQJsoktQkeE5wIOuihvElwOyVUQfEtwb9FK-b0_-i2H-rtxBdP8KJyznJRjurM4qMMKhC8WTK7bHEWk_YPv0sZYVKVcw, token_type=bearer, refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2NzYyODU4MDMsInVzZXJfbmFtZSI6ImJvYiIsImp0aSI6ImQwMWVhM2U1LWFmMjItNDk0NC05ZTM2LWJkMDZhODhlNGVlOCIsImNsaWVudF9pZCI6Im15LXRydXN0ZWQtY2xpZW50Iiwic2NvcGUiOlsicmVhZCIsIndyaXRlIiwidHJ1c3QiXSwiYXRpIjoiNDYxOTA2NzYtODE3MC00NDgwLWE3ZDktODFlNWRjMTU0MTIzIn0.buBD6yxJpI7JFY6LKw9X_3Ice-m5o0gFNIe4w-yd4izWvZx5Zsq9gOsHbBAYlmdy0PVH2-tybKn9cB3LPFkMFn0feujA6h-vpb897qEHkWH9uXs5UPX24Ky9wNOCTshPEoZVN2afZ__8c_a9SsI0aHlxSoDKfLeCgzsdlrePFgULl2-IDTB9rHT80fb8rAQiuxiDF7F1dYorBX5Ag9WxNAnE0tf4YZQjJxapVWZ1fTyZ6rbLNqwqyHep98vr_hySVNJqO2O8XyWVPOwnnNaBIPUDumsZOl_qtC41dRfrvlgiD9lQHbrN3mp2YEAS2NOR9kp5e8DuG3DPLTwea2LRfQ";
		final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		String certificatePassword ="rewq43214@$%";
//		// 设置默认值
//		if (StringUtils.isEmpty(signingkey)) {
//			signingkey = "awbeci";
//		}
//		// 密钥，放到配置文件中
//		jwtAccessTokenConverter.setSigningKey(signingkey);
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("console.jks"),
				certificatePassword.toCharArray());
		jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair("console"));
		
		System.out.println(jwtAccessTokenConverter.extractAccessToken(value, jwtAccessTokenConverter.getKey()).getValue());
		
	}
	
	protected static String extractTokenKey(String value) {
		if (value == null) {
			return null;
		}
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
		}

		try {
			byte[] bytes = digest.digest(value.getBytes("UTF-8"));
			return String.format("%032x", new BigInteger(1, bytes));
		}
		catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
		}
	}

}
