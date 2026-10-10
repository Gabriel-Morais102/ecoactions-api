package br.com.fiap.ecoactions_api.dto.response;

import br.com.fiap.ecoactions_api.model.Coleta;

import java.time.LocalDate;

public record ColetaResponse(
        String id,
        String pontoColetaId,
        String localPontoColeta,
        String tipoResiduo,
        LocalDate dataColeta,
        Integer quantidadeColeta
) {

    public static ColetaResponse fromEntity(Coleta coleta) {
        return new ColetaResponse(
                coleta.getId(),
                coleta.getPontoColeta().getId(),
                coleta.getPontoColeta().getLocal(),
                coleta.getPontoColeta().getResiduo().getTipoResiduo(),
                coleta.getDataColeta(),
                coleta.getQuantidadeColeta()
        );
    }
}
