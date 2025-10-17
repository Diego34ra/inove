package br.edu.ifgoiano.inove.security;

import br.edu.ifgoiano.inove.controller.exceptions.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    SecurityFilter securityFilter;

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // <— usa o bean abaixo
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // <— preflight

                        // SWAGGER
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()

                        // AUTENTICAÇÃO
                        .requestMatchers(HttpMethod.POST, "/api/inove/auth/**").permitAll()

                        // USUÁRIOS
                        .requestMatchers(HttpMethod.POST, "/api/inove/usuarios/discente").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/inove/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/inove/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inove/usuarios/instrutor/confirmar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inove/usuarios/{userId}/cursos").permitAll()

                        // ESCOLAS
                        .requestMatchers(HttpMethod.POST, "/api/inove/escolas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inove/escolas/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/inove/escolas/**").permitAll()

                        // CURSOS
                        .requestMatchers(HttpMethod.GET, "/api/inove/cursos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/inove/cursos/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/inove/cursos/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/inove/cursos/**").permitAll()

                        // UPLOAD DE IMAGEM DE CURSOS
                        //  .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/upload-imagem-curso").hasRole("INSTRUCTOR")
                        // .requestMatchers(HttpMethod.GET, "/api/inove/cursos/{courseId}/preview-imagem").permitAll()

                        // SEÇÕES
                        // .requestMatchers(HttpMethod.GET, "/api/inove/cursos/{courseId}/secoes/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMINISTRATOR")
                        // .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/secoes/**").hasRole("INSTRUCTOR")
                        // .requestMatchers(HttpMethod.PUT, "/api/inove/cursos/{courseId}/secoes/**").hasRole("INSTRUCTOR")
                        // .requestMatchers(HttpMethod.DELETE, "/api/inove/cursos/{courseId}/secoes/**").hasRole("INSTRUCTOR")

                        // CONTEÚDO
                        // .requestMatchers(HttpMethod.GET, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMINISTRATOR")
                        // .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos").hasRole("INSTRUCTOR")
                        // .requestMatchers(HttpMethod.POST, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos").hasRole("INSTRUCTOR")
                        // .requestMatchers(HttpMethod.PUT, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasRole("INSTRUCTOR")
                        // .requestMatchers(HttpMethod.DELETE, "/api/inove/cursos/{courseId}/secoes/{sectionId}/conteudos/**").hasRole("INSTRUCTOR")

                        // UPLOAD DE CONTEÚDO (PDF/VÍDEO)

                        // STREAM DE CONTEÚDO
                        //.requestMatchers(HttpMethod.GET, "/api/inove/cursos/secoes/conteudos/stream/{fileName}").permitAll()

                        // FEEDBACKS
                        .requestMatchers(HttpMethod.GET, "/api/inove/feedbacks/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/inove/feedbacks/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/inove/feedbacks/{feedbackId}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/inove/feedbacks/{feedbackId}").permitAll()

                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(accessDeniedException()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationEntryPoint accessDeniedException() {
        return new CustomAuthenticationEntryPoint();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);


        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",
                "https://inove.blog.br",
                "https://www.inove.blog.br",
                "https://*.vercel.app"
        ));

        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cfg.setAllowedHeaders(List.of(
                "Authorization","Content-Type","X-Requested-With",
                "Origin","Accept","Access-Control-Request-Method","Access-Control-Request-Headers"
        ));
        cfg.setExposedHeaders(List.of("Content-Disposition"));
        cfg.setMaxAge(3600L); // cache do preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }


}
