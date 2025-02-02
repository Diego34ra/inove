package br.edu.ifgoiano.inove.security;

import br.edu.ifgoiano.inove.controller.exceptions.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    SecurityFilter securityFilter;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/inove/auth/**").permitAll()

                                // Usuários
                                .requestMatchers(HttpMethod.POST, "/api/inove/usuarios/discente").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/inove/usuarios/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/inove/usuarios/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/inove/usuarios/instrutor/confirmar").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/inove/escolas/").permitAll()

                                // Escolas
                                .requestMatchers(HttpMethod.POST, "/api/inove/escolas/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/inove/escolas/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/inove/escolas/**").permitAll()

                                // Cursos
                                .requestMatchers(HttpMethod.GET, "/api/inove/cursos/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/inscreverse").hasRole("STUDENT")
                                .requestMatchers(HttpMethod.DELETE, "/api/inove/usuarios/{userId}/cursos/{courseId}").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/api/inove/cursos/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/inove/cursos/**").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.DELETE, "/api/inove/cursos/**").hasRole("ADMINISTRATOR")
                                .requestMatchers(HttpMethod.GET, "/api/inove/usuarios/{userId}/cursos").permitAll()


                                // Seções
                                .requestMatchers(HttpMethod.GET, "/api/inove/cursos/{courseId}/secoes/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMINISTRATOR")
                                .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/secoes/**").hasRole("INSTRUCTOR")
                                .requestMatchers(HttpMethod.PUT, "/api/inove/cursos/{courseId}/secoes/**").hasRole("INSTRUCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/api/inove/cursos/{courseId}/secoes/**").hasRole("INSTRUCTOR")

                                // Conteúdo
                                .requestMatchers(HttpMethod.GET, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMINISTRATOR")
                                .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasRole("INSTRUCTOR")
                                .requestMatchers(HttpMethod.PUT, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasRole("INSTRUCTOR")
                                .requestMatchers(HttpMethod.DELETE, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasRole("INSTRUCTOR")

                                .anyRequest().authenticated()
                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(accessDeniedException()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationEntryPoint accessDeniedException(){
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
