package com.tdh.gps.console.token.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.tdh.gps.console.model.Role;
import com.tdh.gps.console.model.UserInfo;

/**
 * 
 * @ClassName: UserDetailsServiceImpl
 * @Description: (userDetailsService实现类)
 * @author wxf
 * @date 2018年12月4日 下午4:06:42
 *
 */
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService, InitializingBean {

//	@Autowired
//	private BeanInitConfig beanInitConfig;
//	@Reference(timeout = 10000)
//	UserService userService;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Bean
	@Autowired
	public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		List<Map<String, Object>> result = jdbcTemplate.queryForList(
				"select user_name,password,r.role_name from user_info u INNER JOIN user_role_relation rel ON u.id=rel.user_id INNER JOIN role r ON rel.role_id=r.id  where user_name=?",
				new Object[] { username });
		List<Role> roles = new ArrayList<Role>();
		if (CollectionUtils.isNotEmpty(result)) {
			for (Map<String, Object> map : result) {
				Role role = new Role(map.get("role_name").toString());
				roles.add(role);
			}
			return new UserInfo(result.get(0).get("user_name").toString(), result.get(0).get("password").toString(),
					roles);
		}
		return null;
//		User user = beanInitConfig.getUserService().loadUserByUsername(username);
//		return null != user ? new WdcyUserDetails(user) : null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(jdbcTemplate, "JdbcTemplate cannot be empty !");

	}
}
