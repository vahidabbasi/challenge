package com.celonis.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
    private final String HEADER_NAME = "Celonis-Auth";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo())
                .globalOperationParameters(globalParameterList())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.celonis.challenge.controllers"))
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("Task service")
                .description("Micro-service for handle task execution")
                .version("1.0.0")
                .build();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    private List<Parameter> globalParameterList() {
        var authTokenHeader =
                new ParameterBuilder()
                        .name(HEADER_NAME) // name of the header
                        .modelRef(new ModelRef("string")) // data-type of the header
                        .required(true) // required/optional
                        .parameterType("header") // for query-param, this value can be 'query'
                        .description("Basic Auth Token")
                        .build();

        return Collections.singletonList(authTokenHeader);
    }


}
