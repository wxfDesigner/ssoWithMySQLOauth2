package com.tdh.gps.console.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.tdh.gps.console.dto.UserOverviewDto;
import com.tdh.gps.console.model.OauthClientDetails;
import com.tdh.gps.console.model.User;
import com.tdh.gps.console.resource.configuration.BeanInitConfig;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class ApplicationTest {

	Logger logger = LoggerFactory.getLogger(ApplicationTest.class);

	@Autowired
	BeanInitConfig beanInitConfig;
//	@Reference
//	UserService userService;
	Gson gson = new Gson();

	@Test
	public void loadUserByUsername() {
		UserOverviewDto overviewDto = new UserOverviewDto();
		overviewDto.setUsername("bob");
		User userDetails = beanInitConfig.getUserService().loadUserByUsername("bob");

		logger.info(gson.toJson(userDetails));
	}

	@Test
	public void loadClientByClientId() {
		OauthClientDetails oauthClientDetails = beanInitConfig.getAuthorizationService()
				.findOauthClientDetails("5c08d89d0cac8b94510457e0");
		logger.info(gson.toJson(oauthClientDetails));
	}
}
