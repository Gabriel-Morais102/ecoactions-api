package br.com.fiap.ecoactions_api.dto.response;

import br.com.fiap.ecoactions_api.model.PontoColeta;

public record PontoColetaResponse(
        String id,
        String residuoId,
        String tipoResiduo,
        String local,
        Integer capacidadeAtual,
        Integer capacidadeMaxima,
        boolean limiteAtingido
) {

    public static PontoColetaResponse fromEntity(PontoColeta pontoColeta) {
        return new PontoColetaResponse(
                pontoColeta.getId(),
                pontoColeta.getResiduo().getId(),
                pontoColeta.getResiduo().getTipoResiduo(),
                pontoColeta.getLocal(),
                pontoColeta.getCapacidadeAtual(),
                pontoColeta.getCapacidadeMaxima(),
                pontoColeta.getCapacidadeAtual() >= pontoColeta.getCapacidadeMaxima()
        );
    }
}
