package br.com.fiap.ecoactions_api.dto.response;

import br.com.fiap.ecoactions_api.model.Destinacao;

public record DestinacaoResponse(
        String id,
        String coletaId,
        String pontoColetaId,
        String localPontoColeta,
        String tipoResiduo,
        String tipoDestino
) {

    public static DestinacaoResponse fromEntity(Destinacao destinacao) {
        var coleta = destinacao.getColeta();
        var pontoColeta = coleta.getPontoColeta();

        return new DestinacaoResponse(
                destinacao.getId(),
                coleta.getId(),
                pontoColeta.getId(),
                pontoColeta.getLocal(),
                pontoColeta.getResiduo().getTipoResiduo(),
                destinacao.getTipoDestino()
        );
    }
}
