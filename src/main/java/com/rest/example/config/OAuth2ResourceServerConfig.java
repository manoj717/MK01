package com.rest.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class OAuth2ResourceServerConfig {


    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http, final CustomExceptionResolver customExceptionResolver){

        try{
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health").anonymous()
                    .anyRequest().authenticated()
            ).oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(Customizer.withDefaults()).authenticationEntryPoint(customExceptionResolver));
            return http.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
