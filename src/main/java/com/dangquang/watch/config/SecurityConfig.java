package com.dangquang.watch.config;

import com.dangquang.watch.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/", "/shop", "/watch/**", "/login", "/register", "/error", "/access-denied",
                               "/css/**", "/js/**", "/images/**", 
                               "/favicon.ico", "/webjars/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cart/**", "/order/**", "/payment/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    response.sendRedirect("/admin");
                    return;
                }
            }
            
            // Default redirect for USER role
            response.sendRedirect("/");
        };
    }

}
