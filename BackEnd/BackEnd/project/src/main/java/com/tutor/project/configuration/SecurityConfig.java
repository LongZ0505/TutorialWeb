package com.tutor.project.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] Public_EndPoints = {
            "/users/registry", "/auth/**","/course"
    };
    @Autowired
    private CustomDecoder customDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(registry ->
                registry.requestMatchers(Public_EndPoints).permitAll()
                        .requestMatchers(HttpMethod.GET,"/courses").permitAll()
                        .anyRequest().authenticated());
        http.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                httpSecurityOAuth2ResourceServerConfigurer.jwt(
                                jwtConfigurer -> jwtConfigurer.decoder(customDecoder)).
                        authenticationEntryPoint(new jwtAuthenticationEntryPoint()));
        http.csrf(AbstractHttpConfigurer::disable);
       return http.build();
    }
    @Bean
    public PasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(8);
    }
}
