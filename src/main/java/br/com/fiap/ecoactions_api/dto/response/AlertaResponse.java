package br.com.fiap.ecoactions_api.dto.response;

import br.com.fiap.ecoactions_api.model.Alerta;

import java.time.LocalDate;

public record AlertaResponse(
        String id,
        String mensagem,
        LocalDate dataAlerta
) {

    public static AlertaResponse fromEntity(Alerta alerta) {
        return new AlertaResponse(
                alerta.getId(),
                alerta.getMensagem(),
                alerta.getDataAlerta()
        );
    }
}
