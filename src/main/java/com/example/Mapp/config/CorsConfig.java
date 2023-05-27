package com.example.Mapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Dozvoli pristup sa svih izvora, možete prilagoditi prema svojim potrebama
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Dozvoli određene HTTP metode
                .allowedHeaders("Content-Type", "Authorization") // Dozvoli određene zaglavlja
                //.allowCredentials(true) // Dozvoli slanje kredencijala (npr. korišćenje sesija)
                .maxAge(3600); // Postavi vreme trajanja CORS konfiguracije
    }
}
