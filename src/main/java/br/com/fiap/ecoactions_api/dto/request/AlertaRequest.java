package br.com.fiap.ecoactions_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlertaRequest(
        @NotBlank(message = "A mensagem do alerta é obrigatória.")
        @Size(min = 5, max = 200, message = "A mensagem do alerta deve ter entre 5 e 200 caracteres.")
        String mensagem,

        LocalDate dataAlerta
) {
}
