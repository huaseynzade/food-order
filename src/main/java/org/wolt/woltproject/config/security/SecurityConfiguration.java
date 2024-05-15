package org.wolt.woltproject.config.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfiguration {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(permitAll).permitAll()
                                .requestMatchers("/auth/register", "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/user").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.DELETE, "/user/").hasAnyRole("ADMIN","CUSTOMER")
                                .requestMatchers("/user/**").hasRole("ADMIN")
                                .requestMatchers("/review/{restaurantId}/all").hasAnyRole("RESTAURANT", "CUSTOMER","ADMIN")
                                .requestMatchers("/review/**").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.POST,"/restaurant").hasRole("RESTAURANT")
                                .requestMatchers("/restaurant/confirm/**").hasRole("RESTAURANT")
                                .requestMatchers(HttpMethod.DELETE,"/restaurant/{id}").hasAnyRole("RESTAURANT", "ADMIN")
                                .requestMatchers("/restaurant/**").permitAll()
                                .requestMatchers("/payment/**").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.GET,"/payment/{id}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET,"/payment/{userId}/history").hasRole("ADMIN")
                                .requestMatchers("/order/{restaurantId}/prepare/{orderId}").hasRole("RESTAURANT")
                                .requestMatchers("/order/{courierId}/take/**","/order/deliver/**").hasRole("COURIER")
                                .requestMatchers("/order/**").hasRole("CUSTOMER")
                                .requestMatchers(HttpMethod.POST,"/menu").hasAnyRole("ADMIN","RESTAURANT")
                                .requestMatchers(HttpMethod.PUT,"/menu/**").hasAnyRole("ADMIN","RESTAURANT")
                                .requestMatchers(HttpMethod.DELETE,"/menu/**").hasAnyRole("ADMIN","RESTAURANT")
                                .requestMatchers("/menu/**").permitAll()
                                .requestMatchers(HttpMethod.PUT,"/menu-item/{id}").hasAnyRole("ADMIN","RESTAURANT")
                                .requestMatchers(HttpMethod.DELETE,"/menu-item/{id}").hasAnyRole("ADMIN","RESTAURANT")
                                .requestMatchers(HttpMethod.POST,"/menu-item").hasAnyRole("ADMIN","RESTAURANT")
                                .requestMatchers("/menu-item/**").permitAll()
                                .requestMatchers("/auth/send","/auth/activation/**").authenticated()
                                .requestMatchers("/card/**").hasAnyRole("CUSTOMER","ADMIN")
                                .anyRequest().authenticated()
                ).exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN)
                        )
                );
        return http.build();

    }

    public static String[] permitAll = {
            "/api/v1/auth/**",
            "v3/api-docs/**",
            "v3/api-docs.yaml",
            "swagger-ui/**",
            "swagger-ui.html",
            "/auth/register",
            "/auth/login"
    };

}
