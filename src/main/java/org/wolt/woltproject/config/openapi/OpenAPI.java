package org.wolt.woltproject.config.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@OpenAPIDefinition(

        info = @Info(
                contact = @Contact(
                        name = "Rebbin",
                        email = "huaseynzade@gmail.com"

                ),
                title = "Wolt Project",
                description = "My Final Project for Matrix",
                version = "1.9"
        ),
        servers = {
                @Server(
                        description = "For Connect",
                        url = "http://localhost:8080"
                )
        },
        security = {
        @SecurityRequirement(
                name = "bearerAuth"
        )
})
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPI {
}
