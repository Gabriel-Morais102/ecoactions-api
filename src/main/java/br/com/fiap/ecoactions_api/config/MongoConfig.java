package br.com.fiap.ecoactions_api.config;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig {

    @Bean
    public MongoClientSettingsBuilderCustomizer mongoTimeoutCustomizer() {
        // Evita que uma indisponibilidade do banco mantenha o health check aguardando por 30 segundos.
        return builder -> builder
                .applyToClusterSettings(settings -> settings.serverSelectionTimeout(2, TimeUnit.SECONDS))
                .applyToSocketSettings(settings -> settings
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS));
    }
}
