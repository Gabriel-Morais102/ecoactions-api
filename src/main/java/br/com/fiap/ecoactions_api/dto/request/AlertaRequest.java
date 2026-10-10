package br.com.fiap.ecoactions_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlertaRequest(
        @NotBlank(message = "A mensagem do alerta é obrigatória.")
        @Size(max = 200, message = "A mensagem do alerta deve ter no máximo 200 caracteres.")
        String mensagem,

        LocalDate dataAlerta
) {
}
