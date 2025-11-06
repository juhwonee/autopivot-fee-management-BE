package com.example.capstonedesign20252.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI(){
    return new OpenAPI()
        .info(new Info()
            .title("AutoFeeBot API")
            .description("오토피봇 API 문서")
            .version("v1.0.0"));
  }
}

