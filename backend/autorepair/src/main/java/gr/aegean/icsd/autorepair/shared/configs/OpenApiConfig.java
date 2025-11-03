package gr.aegean.icsd.autorepair.shared.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.beans.factory.annotation.Value;


@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Dionysis Theodosis"
                ),
                description = "OpenApi documentation for AutoRepair-WebDevelopment Project 2025",
                title = "OpenApi specification - AutoRepair - Dionysis Theodosis"
        ),
        servers = {
                @Server(
                        description ="Local ENV",
                        url = "${openapi.server.url}"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "jwtToken"
                )
        }
)
@SecurityScheme(
        name = "jwtToken",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.COOKIE,
        paramName = "jwtToken",
        description = "JWT Token stored in an HTTP-only cookie for Authentication"
)
public class OpenApiConfig {
        @Value("${openapi.server.url}")
        private String serverUrl;
}