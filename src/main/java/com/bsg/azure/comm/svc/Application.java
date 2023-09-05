package com.bsg.azure.comm.svc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableAspectJAutoProxy
public class Application {

	public static final String CURRENT_SCHEMA_BSG = "currentSchema=bsg";

	public static final String CURRENT_SCHEMA_PUBLIC = "currentSchema=public";

	public static final String DB_PROPS_PLATFORM_KEY = "platform";

	public static final String SESSION_FACTORY_BEAN_CAMUNDA = "camundaSqlSessionFactory";

	public static final String SESSION_FACTORY_BEAN_CARE_MGMT = "publicSqlSessionFactory";

	private static final String CORS_CONFIG_ORIGIN_LOCAL_HTTPS = "https://localhost:4200";

	private static final String CORS_CONFIG_ORIGIN_LOCAL_HTTP = "http://localhost:4200";

	private static final String CORS_CONFIG_ORIGIN_DEV_HTTPS = "https://dev.bsg-azure.com";

	private static final String CORS_CONFIG_ORIGIN_TEST_HTTPS = "https://test.bsg-azure.com";

	private static final String CORS_CONFIG_ORIGIN_STAGE_HTTPS = "https://stage.bsg-azure.com";

	private static final String CORS_CONFIG_ORIGIN_ALL = "*";

	private static final String CORS_CONFIG_HEADER_ALL = "*";

	private static final String CORS_CONFIG_METHOD_ALL = "*";

	private static final String CORS_CONFIG_REGISTER_PATH_ALL = "/**";

	private static final String CURO_UI_URL_PROPERTY = "cors.ui-url";

	//private static final String CORS_CONFIG_URLS = "CORS_ALLOWED_ORGINS";
	@SuppressWarnings("unused")
	private static final String CORS_CONFIG_URLS = "corsAllowedOrigins";

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	@Autowired
	private Environment env;

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		String curoUiUrl = env.getProperty(CURO_UI_URL_PROPERTY);
		CorsConfiguration configurationLocal = new CorsConfiguration();
		configurationLocal.setAllowCredentials(true);
		configurationLocal.addAllowedOrigin(curoUiUrl);
		configurationLocal.addAllowedOrigin(CORS_CONFIG_ORIGIN_LOCAL_HTTPS);
		configurationLocal.addAllowedOrigin(CORS_CONFIG_ORIGIN_LOCAL_HTTP);
		configurationLocal.addAllowedOrigin(CORS_CONFIG_ORIGIN_DEV_HTTPS);
		configurationLocal.addAllowedOrigin(CORS_CONFIG_ORIGIN_TEST_HTTPS);
		configurationLocal.addAllowedOrigin(CORS_CONFIG_ORIGIN_STAGE_HTTPS);
		configurationLocal.addAllowedOrigin(CORS_CONFIG_ORIGIN_ALL);
		configurationLocal.addAllowedHeader(CORS_CONFIG_HEADER_ALL);
		configurationLocal.addAllowedMethod(CORS_CONFIG_METHOD_ALL);
		source.registerCorsConfiguration(CORS_CONFIG_REGISTER_PATH_ALL, configurationLocal);

		return source;
	}


	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
