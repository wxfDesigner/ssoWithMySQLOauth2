package com.tdh.gps.console.token.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.tdh.gps.console.utils.MD5ExtractTokenKeyDigesterUtils;

/**
 * 
 * @ClassName: MongoTokenStore
 * @Description: (mongodbToken令牌存储service实现类)
 * @author wxf
 * @date 2018年12月6日 下午3:19:26
 *
 */
//@Component("mysqlTokenStore")
public class MysqlTokenStore implements TokenStore, InitializingBean {

	private static final Logger LOG = LoggerFactory.getLogger(MysqlTokenStore.class);

	private static final String DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT = "insert into oauth_access_token (token_id, token, authentication_id, user_name, client_id, authentication, refresh_token) values (?, ?, ?, ?, ?, ?, ?)";

	private static final String DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT = "select token_id, token from oauth_access_token where token_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from oauth_access_token where token_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT = "select token_id, token from oauth_access_token where authentication_id = ?";

	private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT = "select token_id, token from oauth_access_token where user_name = ? and client_id = ?";

	private static final String DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT = "select token_id, token from oauth_access_token where user_name = ?";

	private static final String DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT = "select token_id, token from oauth_access_token where client_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT = "delete from oauth_access_token where token_id = ?";

	private static final String DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT = "delete from oauth_access_token where refresh_token = ?";

	private static final String DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT = "insert into oauth_refresh_token (token_id, token, authentication) values (?, ?, ?)";

	private static final String DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT = "select token_id, token from oauth_refresh_token where token_id = ?";

	private static final String DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT = "select token_id, authentication from oauth_refresh_token where token_id = ?";

	private static final String DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT = "delete from oauth_refresh_token where token_id = ?";

	private String insertAccessTokenSql = DEFAULT_ACCESS_TOKEN_INSERT_STATEMENT;

	private String selectAccessTokenSql = DEFAULT_ACCESS_TOKEN_SELECT_STATEMENT;

	private String selectAccessTokenAuthenticationSql = DEFAULT_ACCESS_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

	private String selectAccessTokenFromAuthenticationSql = DEFAULT_ACCESS_TOKEN_FROM_AUTHENTICATION_SELECT_STATEMENT;

	private String selectAccessTokensFromUserNameAndClientIdSql = DEFAULT_ACCESS_TOKENS_FROM_USERNAME_AND_CLIENT_SELECT_STATEMENT;

	private String selectAccessTokensFromUserNameSql = DEFAULT_ACCESS_TOKENS_FROM_USERNAME_SELECT_STATEMENT;

	private String selectAccessTokensFromClientIdSql = DEFAULT_ACCESS_TOKENS_FROM_CLIENTID_SELECT_STATEMENT;

	private String deleteAccessTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_STATEMENT;

	private String insertRefreshTokenSql = DEFAULT_REFRESH_TOKEN_INSERT_STATEMENT;

	private String selectRefreshTokenSql = DEFAULT_REFRESH_TOKEN_SELECT_STATEMENT;

	private String selectRefreshTokenAuthenticationSql = DEFAULT_REFRESH_TOKEN_AUTHENTICATION_SELECT_STATEMENT;

	private String deleteRefreshTokenSql = DEFAULT_REFRESH_TOKEN_DELETE_STATEMENT;

	private String deleteAccessTokenFromRefreshTokenSql = DEFAULT_ACCESS_TOKEN_DELETE_FROM_REFRESH_TOKEN_STATEMENT;

	private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public MysqlTokenStore() {
	}

	@Override
	public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
		final String authenticationId = authenticationKeyGenerator.extractKey(authentication);
		OAuth2AccessToken accessToken = null;

		try {
			accessToken = jdbcTemplate.queryForObject(selectAccessTokenFromAuthenticationSql,
					new RowMapper<OAuth2AccessToken>() {
						public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
							return deserializeAccessToken(rs.getBytes(2));
						}
					}, authenticationId);
		} catch (EmptyResultDataAccessException e) {
			LOG.debug("Failed to find access token for authentication " + authentication);
		} catch (IllegalArgumentException e) {
			LOG.error("Could not extract access token for authentication {}", authentication);
		}

		if (accessToken != null && !authenticationId
				.equals(authenticationKeyGenerator.extractKey(readAuthentication(accessToken.getValue())))) {
			removeAccessToken(accessToken.getValue());
			// Keep the store consistent (maybe the same user is represented by this
			// authentication but the details have
			// changed)
			storeAccessToken(accessToken, authentication);
		}

		return accessToken;
	}

	@Override
	public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
		LOG.debug("Call storeAccessToken, token = {}, authentication = {}", token, authentication);
		String refreshToken = token.getRefreshToken() != null ? token.getRefreshToken().getValue() : null;

//		AccessToken accessToken = new AccessToken().tokenId(extractTokenKey(token.getValue())).token(token)
//				.authenticationId(authenticationKeyGenerator.extractKey(authentication))
//				.username(authentication.isClientOnly() ? null : authentication.getName())
//				.clientId(authentication.getOAuth2Request().getClientId()).authentication(authentication)
//				.refreshToken(extractTokenKey(refreshToken));

		jdbcTemplate.update(insertAccessTokenSql,
				new Object[] { extractTokenKey(token.getValue()), new SqlLobValue(serializeAccessToken(token)),
						authenticationKeyGenerator.extractKey(authentication),
						authentication.isClientOnly() ? null : authentication.getName(),
						authentication.getOAuth2Request().getClientId(),
						new SqlLobValue(serializeAuthentication(authentication)), extractTokenKey(refreshToken) },
				new int[] { Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB,
						Types.VARCHAR });
	}

	@Override
	public OAuth2AccessToken readAccessToken(String tokenValue) {
		LOG.debug("Call readAccessToken, tokenValue = {}", tokenValue);
		OAuth2AccessToken token = null;

		try {
//			final String tokenId = extractTokenKey(tokenValue);

			token = jdbcTemplate.queryForObject(selectAccessTokenSql, new RowMapper<OAuth2AccessToken>() {
				public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
					return deserializeAccessToken(rs.getBytes(2));
				}
			}, extractTokenKey(tokenValue));
		} catch (EmptyResultDataAccessException e) {
			LOG.info("Failed to find access token for token " + tokenValue);
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize access token for {}", tokenValue);
			removeAccessToken(tokenValue);
		}

		return token;
	}

	@Override
	public void removeAccessToken(OAuth2AccessToken token) {
		removeAccessToken(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
		return readAuthentication(token.getValue());
	}

	@Override
	public OAuth2Authentication readAuthentication(String token) {
		LOG.debug("Call readAuthentication, token = {}", token);
		OAuth2Authentication authentication = null;
		try {
//			final String tokenId = extractTokenKey(token);

			authentication = jdbcTemplate.queryForObject(selectAccessTokenAuthenticationSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return deserializeAuthentication(rs.getBytes(2));
						}
					}, extractTokenKey(token));

		} catch (EmptyResultDataAccessException e) {
			LOG.info("Failed to find access token for token " + token);
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize authentication for {}", token);
			removeAccessToken(token);
		}

		return authentication;
	}

	protected void removeAccessToken(String tokenValue) {
//		final String tokenId = extractTokenKey(tokenValue);
		jdbcTemplate.update(deleteAccessTokenSql, extractTokenKey(tokenValue));
	}

	@Override
	public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
		LOG.debug("Call storeRefreshToken, refreshToken = {}, authentication = {}", refreshToken, authentication);

//		RefreshToken token = new RefreshToken().tokenId(extractTokenKey(refreshToken.getValue())).token(refreshToken)
//				.authentication(authentication);

		jdbcTemplate.update(insertRefreshTokenSql,
				new Object[] { extractTokenKey(refreshToken.getValue()),
						new SqlLobValue(serializeRefreshToken(refreshToken)),
						new SqlLobValue(serializeAuthentication(authentication)) },
				new int[] { Types.VARCHAR, Types.BLOB, Types.BLOB });
	}

	@Override
	public OAuth2RefreshToken readRefreshToken(String tokenValue) {
		LOG.debug("Call readRefreshToken, tokenValue = {}", tokenValue);
		OAuth2RefreshToken refreshToken = null;

		try {
//			final String tokenId = extractTokenKey(tokenValue);

			refreshToken = jdbcTemplate.queryForObject(selectRefreshTokenSql, new RowMapper<OAuth2RefreshToken>() {
				public OAuth2RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
					return deserializeRefreshToken(rs.getBytes(2));
				}
			}, extractTokenKey(tokenValue));
		} catch (EmptyResultDataAccessException e) {

			LOG.info("Failed to find refresh token for token " + tokenValue);

		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize refresh token for token {}", tokenValue);
			removeRefreshToken(tokenValue);
		}

		return refreshToken;
	}

	@Override
	public void removeRefreshToken(OAuth2RefreshToken token) {
		removeRefreshToken(token.getValue());
	}

	protected void removeRefreshToken(String token) {
//		final String tokenId = extractTokenKey(token);
		jdbcTemplate.update(deleteRefreshTokenSql, extractTokenKey(token));
	}

	@Override
	public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
		return readAuthenticationForRefreshToken(token.getValue());
	}

	protected OAuth2Authentication readAuthenticationForRefreshToken(String tokenValue) {
		OAuth2Authentication authentication = null;

		try {
//			final String tokenId = extractTokenKey(tokenValue);
			authentication = jdbcTemplate.queryForObject(selectRefreshTokenAuthenticationSql,
					new RowMapper<OAuth2Authentication>() {
						public OAuth2Authentication mapRow(ResultSet rs, int rowNum) throws SQLException {
							return deserializeAuthentication(rs.getBytes(2));
						}
					}, extractTokenKey(tokenValue));
		} catch (EmptyResultDataAccessException e) {

			LOG.info("Failed to find access token for token " + tokenValue);

		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to deserialize access token for {}", tokenValue);
			removeRefreshToken(tokenValue);
		}

		return authentication;
	}

	@Override
	public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
		removeAccessTokenUsingRefreshToken(refreshToken.getValue());
	}

	protected void removeAccessTokenUsingRefreshToken(String refreshTokenValue) {
//		final String refreshToken = extractTokenKey(refreshTokenValue);
		jdbcTemplate.update(deleteAccessTokenFromRefreshTokenSql, new Object[] { extractTokenKey(refreshTokenValue) },
				new int[] { Types.VARCHAR });
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
		LOG.debug("Call findTokensByClientId, clientId = {}", clientId);

		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
		try {
			accessTokens = jdbcTemplate.query(selectAccessTokensFromClientIdSql, new SafeAccessTokenRowMapper(),
					clientId);
		} catch (EmptyResultDataAccessException e) {
			LOG.info("Failed to find access token for clientId " + clientId);
		}
		accessTokens = removeNulls(accessTokens);
		return accessTokens;
	}

	@Override
	public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
		LOG.debug("Call findTokensByUserName, clientId = {}, username = {}", clientId, userName);

		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();
		try {
			accessTokens = jdbcTemplate.query(selectAccessTokensFromUserNameAndClientIdSql,
					new SafeAccessTokenRowMapper(), userName, clientId);
		} catch (EmptyResultDataAccessException e) {

			LOG.info("Failed to find access token for clientId " + clientId + " and userName " + userName);

		}
		accessTokens = removeNulls(accessTokens);
		return accessTokens;
	}

	public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
		LOG.debug("Call findTokensByUserName, userName = {}", userName);
		List<OAuth2AccessToken> accessTokens = new ArrayList<OAuth2AccessToken>();

		try {
			accessTokens = jdbcTemplate.query(selectAccessTokensFromUserNameSql, new SafeAccessTokenRowMapper(),
					userName);
		} catch (EmptyResultDataAccessException e) {
			LOG.info("Failed to find access token for userName " + userName);
		}
		accessTokens = removeNulls(accessTokens);

		return accessTokens;
	}

	protected byte[] serializeAccessToken(OAuth2AccessToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
		return SerializationUtils.serialize(token);
	}

	protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
		return SerializationUtils.serialize(authentication);
	}

	protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
		return SerializationUtils.deserialize(token);
	}

	protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
		return SerializationUtils.deserialize(authentication);
	}

	protected String extractTokenKey(String value) {
		return MD5ExtractTokenKeyDigesterUtils.digest(value);
	}

	private final class SafeAccessTokenRowMapper implements RowMapper<OAuth2AccessToken> {
		public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
			try {
				return deserializeAccessToken(rs.getBytes(2));
			} catch (IllegalArgumentException e) {
				String token = rs.getString(1);
				jdbcTemplate.update(deleteAccessTokenSql, token);
				return null;
			}
		}
	}

	private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
		List<OAuth2AccessToken> tokens = new ArrayList<OAuth2AccessToken>();
		for (OAuth2AccessToken token : accessTokens) {
			if (token != null) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public void setAuthenticationKeyGenerator(AuthenticationKeyGenerator authenticationKeyGenerator) {
		this.authenticationKeyGenerator = authenticationKeyGenerator;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(jdbcTemplate, "JdbcTemplate cannot be empty !");
	}
}
