package com.tus.musicapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tus.musicapp.filter.JwtRequestFilter;
import com.tus.musicapp.service.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity // Enables method-level security (e.g., @PreAuthorize)
public class SecurityConfig {
    
    @Autowired
    private UserDetailsServiceImpl myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
        .csrf(csrf -> csrf.disable()) // Disable CSRF (needed for REST APIs)
        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // Allow H2 Console
        .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            // ✅ ALLOW STATIC FILES (Vue frontend)
            .antMatchers("/", "/index.html", "/js/**", "/pages/**", "/assets/**", "/favicon.ico").permitAll()
            .antMatchers("/api/carousel-images").authenticated()
            // ✅ Allow Authentication & H2 Console
            .antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/music/**").permitAll()
            .antMatchers("/h2-console/**").permitAll()

            // ✅ Restrict Admin Registration to Admins
            .antMatchers("/api/admin/register").hasAuthority("ADMIN") 

            // ✅ Restrict Components by Role
            //.antMatchers("/pages/admin/**").hasAuthority("ADMIN")

            // ✅ Require authentication for everything else
            .anyRequest().authenticated()
        );

    // ✅ Ensure JWT filter is applied before username/password authentication
    http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();

    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
