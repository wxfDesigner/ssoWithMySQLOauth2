package com.tdh.gps.console.common.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger工具初始化配置类
 * 
 * @author wangxf
 *
 */
@Configuration
@EnableOpenApi
public class Swagger3Config {

	@Value("${spring.application.name}")
	private String applicationName;

	/**
	 * 创建API应用 apiInfo() 增加API相关信息
	 * 通过select()函数返回一个ApiSelectorBuilder实例,用来控制哪些接口暴露给Swagger来展现，
	 * 本例采用指定扫描的包路径来定义指定要建立API的目录。
	 * 
	 * @return
	 */
	@Bean
	public Docket createRestApi() {
		// 使用3.0文档
		return new Docket(DocumentationType.OAS_30)
				// 创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
				.apiInfo(apiInfo())
				// 设置哪些接口暴露给Swagger展示
				.select()
				// 扫描指定包中的swagger注解 扫描所有 (这里也有很大不同切记)
//				.apis(RequestHandlerSelectors.withMethodAnnotation(Operation.class))
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build().globalRequestParameters(setHeaderToken());
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
////                .apis(RequestHandlerSelectors.basePackage("com.tdh.gps.console"))
//                .apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.any())
//                .build().globalOperationParameters(setHeaderToken());
	}

	/**
	 * 创建该API的基本信息（这些基本信息会展现在文档页面中） 访问地址：http://项目实际地址/swagger-ui.html
	 * 
	 * @return
	 */
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title(applicationName + "应用服务").description("SpringCloud 中使用Swagger3构建RESTful APIs")
				.termsOfServiceUrl("https://github.com/wxfDesigner/").license("证书")// 证书
				.licenseUrl("https://github.com/wxfDesigner/")// 证书地址
				.contact(new Contact("人笑我我笑人生","https://github.com/wxfDesigner/","15821434334@163.com"))
				.version("1.0").build();
	}

	/**
	 * @Description: 设置swagger文档中全局参数
	 * @param
	 * @Date: 2020/9/11 10:15
	 * @return: java.util.List<springfox.documentation.service.Parameter>
	 */
	private List<RequestParameter> setHeaderToken() {
		List<RequestParameter> params = new ArrayList<>();
		params.add(new RequestParameterBuilder()
		        .name("access_token")
		        .description("用户TOKEN")
		        .required(true)
		        .in(ParameterType.QUERY)
		        .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
//		        .required(false)
		        .build());
		return params;
	}
	/**
	 * 设置多个：
	 *
	 * @Bean public Docket appApi() {
	 *
	 *       List<Parameter> pars = new ArrayList<>(); ParameterBuilder token = new
	 *       ParameterBuilder();
	 *       token.name("token").description("用户令牌").modelRef(new
	 *       ModelRef("string")).parameterType("header").required(false) .build();
	 *       pars.add(token.build());
	 *
	 *       return new
	 *       Docket(DocumentationType.SWAGGER_2).select().paths(regex("/app/.*")).build()
	 *       .globalOperationParameters(pars).apiInfo(pdaApiInfo()).useDefaultResponseMessages(false)
	 *       .enable(enableSwagger) .groupName("appApi");
	 *
	 *       }
	 *
	 * @Bean public Docket adminApi() {
	 *
	 *       List<Parameter> pars = new ArrayList<>(); ParameterBuilder token = new
	 *       ParameterBuilder();
	 *       token.name("token").description("用户令牌").modelRef(new
	 *       ModelRef("string")).parameterType("header").required(false) .build();
	 *       pars.add(token.build()); return new
	 *       Docket(DocumentationType.SWAGGER_2).select().paths(regex("/admin/.*")).build()
	 *       .globalOperationParameters(pars).apiInfo(pdaApiInfo()).useDefaultResponseMessages(false)
	 *       .enable(enableSwagger) .groupName("adminApi");
	 *
	 *       }
	 *
	 *
	 * @return
	 */
}
