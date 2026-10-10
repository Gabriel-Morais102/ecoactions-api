package br.com.fiap.ecoactions_api.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HealthControllerTest {

    private final HealthController healthController = new HealthController();

    @Test
    void deveInformarQueAplicacaoEstaDisponivel() {
        var response = healthController.health();

        assertThat(response)
                .containsEntry("status", "UP")
                .containsEntry("application", "ecoactions-api")
                .containsKey("timestamp");
    }
}
