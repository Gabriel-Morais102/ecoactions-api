package br.com.fiap.ecoactions_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResiduoRequest(
        @NotBlank(message = "O tipo do resíduo é obrigatório.")
        @Size(max = 50, message = "O tipo do resíduo deve ter no máximo 50 caracteres.")
        String tipoResiduo,

        @NotNull(message = "O limite máximo é obrigatório.")
        @Min(value = 1, message = "O limite máximo deve ser maior que zero.")
        Integer limiteMaximo
) {
}
