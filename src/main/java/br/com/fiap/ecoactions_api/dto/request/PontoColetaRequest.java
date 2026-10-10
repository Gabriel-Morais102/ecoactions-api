package br.com.fiap.ecoactions_api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PontoColetaRequest(
        @NotBlank(message = "O id do resíduo é obrigatório.")
        String residuoId,

        @NotBlank(message = "O local do ponto de coleta é obrigatório.")
        @Size(min = 3, max = 100, message = "O local deve ter entre 3 e 100 caracteres.")
        String local,

        @NotNull(message = "A capacidade atual é obrigatória.")
        @Min(value = 0, message = "A capacidade atual não pode ser negativa.")
        Integer capacidadeAtual,

        @NotNull(message = "A capacidade máxima é obrigatória.")
        @Min(value = 1, message = "A capacidade máxima deve ser maior que zero.")
        Integer capacidadeMaxima
) {
}
