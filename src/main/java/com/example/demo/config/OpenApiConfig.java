package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Demo API - Main Issue & Defect Management")
                        .version("1.0.0")
                        .description("""
                                REST API for managing Main Issues and their related Defects.
                                
                                ## Features:
                                - Create, Read, Update, Delete Main Issues
                                - Create, Read, Update, Delete Defects
                                - Cascade deletion: Deleting a Main Issue automatically deletes all related Defects
                                - Full validation and error handling with RFC 7807 Problem Details
                                """)
                        .contact(new Contact()
                                .name("Demo Team")
                                .email("demo@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production Server")
                ));
    }
}

