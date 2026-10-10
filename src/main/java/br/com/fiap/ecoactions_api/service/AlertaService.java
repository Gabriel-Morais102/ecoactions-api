package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.AlertaRequest;
import br.com.fiap.ecoactions_api.dto.response.AlertaResponse;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.model.Alerta;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.repository.AlertaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final PontoColetaService pontoColetaService;

    public List<AlertaResponse> listar() {
        return alertaRepository.findAll()
                .stream()
                .map(AlertaResponse::fromEntity)
                .toList();
    }

    public AlertaResponse buscarPorId(String id) {
        return AlertaResponse.fromEntity(buscarEntidadePorId(id));
    }

    public AlertaResponse cadastrar(AlertaRequest request) {
        var alerta = new Alerta();
        alerta.setMensagem(request.mensagem());
        alerta.setDataAlerta(request.dataAlerta() != null ? request.dataAlerta() : LocalDate.now());

        return AlertaResponse.fromEntity(alertaRepository.save(alerta));
    }

    public AlertaResponse verificarPontoColeta(String pontoColetaId) {
        var pontoColeta = pontoColetaService.buscarEntidadePorId(pontoColetaId);

        if (!capacidadeFoiAtingida(pontoColeta)) {
            var mensagem = "Ponto de coleta '" + pontoColeta.getLocal() + "' ainda possui capacidade disponível.";
            return salvarAlerta(mensagem);
        }

        var mensagem = "Ponto de coleta '" + pontoColeta.getLocal()
                + "' atingiu o limite de capacidade para resíduo do tipo "
                + pontoColeta.getResiduo().getTipoResiduo() + ".";

        return salvarAlerta(mensagem);
    }

    public void remover(String id) {
        var alerta = buscarEntidadePorId(id);
        alertaRepository.delete(alerta);
    }

    private Alerta buscarEntidadePorId(String id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta não encontrado com id: " + id));
    }

    private boolean capacidadeFoiAtingida(PontoColeta pontoColeta) {
        var capacidadeAtual = pontoColeta.getCapacidadeAtual() != null ? pontoColeta.getCapacidadeAtual() : 0;
        return capacidadeAtual >= pontoColeta.getCapacidadeMaxima();
    }

    private AlertaResponse salvarAlerta(String mensagem) {
        var alerta = new Alerta();
        alerta.setMensagem(mensagem);
        alerta.setDataAlerta(LocalDate.now());

        return AlertaResponse.fromEntity(alertaRepository.save(alerta));
    }
}
