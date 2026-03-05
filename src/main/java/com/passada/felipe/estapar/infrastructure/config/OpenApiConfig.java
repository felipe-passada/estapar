package com.passada.felipe.estapar.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI estaparOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Estapar Parking API")
                        .description("Backend system for managing parking spots. "
                                + "Controls available spots, vehicles entry/exit events, "
                                + "dynamic pricing per sector and revenue data.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Felipe Passada")
                                .url("https://github.com/felipe-passada/estapar")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:3003")
                                .description("Local Server")));
    }
}

