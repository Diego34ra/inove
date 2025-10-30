package br.edu.ifgoiano.inove.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Permite todas as origens
        config.addAllowedOriginPattern("*");

        // Permite todos os cabeçalhos
        config.addAllowedHeader("*");

        // Permite todos os métodos
        config.addAllowedMethod("*");

        // Expõe cabeçalhos específicos
        config.addExposedHeader("Access-Control-Allow-Origin");
        config.addExposedHeader("Access-Control-Allow-Methods");
        config.addExposedHeader("Access-Control-Allow-Headers");

        // Define o tempo máximo que o resultado do preflight pode ser cacheado
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
