package br.com.fiap.ecoactions_api.dto.response;

import br.com.fiap.ecoactions_api.model.Residuo;

public record ResiduoResponse(
        String id,
        String tipoResiduo,
        Integer limiteMaximo
) {

    public static ResiduoResponse fromEntity(Residuo residuo) {
        return new ResiduoResponse(
                residuo.getId(),
                residuo.getTipoResiduo(),
                residuo.getLimiteMaximo()
        );
    }
}
