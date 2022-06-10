package com.diltheyaislan.moviesbattle.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@PropertySource("classpath:swagger.properties")
public class SwaggerConfiguration {

	@Value("${api.name}")
	private String apiName;
	
	@Value("${api.description}")
	private String apiDescription;
	
	@Value("${api.version}")
	private String apiVersion;
	
	@Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                		.title(apiName)
                		.description(apiDescription)
                		.version(apiVersion));
    }
}
