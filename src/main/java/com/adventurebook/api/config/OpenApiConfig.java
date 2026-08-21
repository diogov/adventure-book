package com.adventurebook.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI adventureBookOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Adventure Book API")
                                .description("Browse, manage and play through branching adventure gamebooks")
                                .version("v1.0.0"));
    }
}
