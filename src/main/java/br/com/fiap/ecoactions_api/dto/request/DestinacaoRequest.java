package br.com.fiap.ecoactions_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DestinacaoRequest(
        @NotBlank(message = "O id da coleta é obrigatório.")
        String coletaId,

        @NotBlank(message = "O tipo de destino é obrigatório.")
        @Size(min = 3, max = 50, message = "O tipo de destino deve ter entre 3 e 50 caracteres.")
        String tipoDestino
) {
}
