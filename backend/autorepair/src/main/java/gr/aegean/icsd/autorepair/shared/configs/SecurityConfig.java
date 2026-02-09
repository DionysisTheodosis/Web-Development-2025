package gr.aegean.icsd.autorepair.shared.configs;


import gr.aegean.icsd.autorepair.shared.security.service.MyUserDetailsService;
import gr.aegean.icsd.autorepair.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig{
    private final CustomCorsConfiguration customCorsConfiguration;
    private final JwtFilter jwtFilter;
    private final MyUserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.headers(headers ->
                headers.xssProtection(
                        xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                ).contentSecurityPolicy(
                        cps -> cps.policyDirectives("script-src 'self'")
                ));
        http.csrf(AbstractHttpConfigurer::disable).cors(c -> c.configurationSource(customCorsConfiguration));

        http.authorizeHttpRequests(
                request -> {
                    request.requestMatchers(
                            "/",
                            "/signup/**",
                            "/api/v1/auth/isSignIn",
                            "/api/v1/auth/user-role",
                            "/api/v1/auth/login",
                            "/api/v1/auth/register",
                            "/v3/api-docs",
                            "/v3/api-docs/**",
                            "/c",
                            "/swagger-ui/**",
                            "/api/v1/session-cookie",
                            "/api/v1/auth/valid/secretary",
                            "/error"
                    ).permitAll();

                    request.requestMatchers("/secretary/**").hasAuthority(UserRole.SECRETARY.name());
                    request.requestMatchers(
                            "/api/v1/auth/name",
                            "/api/v1/auth/logout",
                            "/api/v1/appointments/**",
                            "/api/v1/users",
                            "/api/v1/users/**",
                            "/api/v1/session-cookie",
                            "/api/v1/auth/me",
                            "/api/v1/dashboard/stats"
                    ).authenticated();

                    request.anyRequest().authenticated();
                }
        );
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
               ;
        http.exceptionHandling(exceptions ->
                exceptions.authenticationEntryPoint(restAuthenticationEntryPoint)
        );
        return http.build();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }


}