package com.example.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Value(value  = "${application.name}")
    private String name;

    @Value(value = "${application.version}")
    private String version;

    @Bean
    public OpenAPI springDoc() {
        return new OpenAPI()
                .info(new Info()
                        .title(name)
                        .version(version))
                .externalDocs(new ExternalDocumentation()
                        .description("API documentation for " + name));
    }
}
