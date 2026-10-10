package br.com.fiap.ecoactions_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ColetaRequest(
        @NotBlank(message = "O id do ponto de coleta é obrigatório.")
        String pontoColetaId,

        LocalDate dataColeta,

        @NotNull(message = "A quantidade coletada é obrigatória.")
        @Min(value = 1, message = "A quantidade coletada deve ser maior que zero.")
        Integer quantidadeColeta
) {
}
