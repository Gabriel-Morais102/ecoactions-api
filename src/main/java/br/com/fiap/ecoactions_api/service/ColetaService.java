package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.ColetaRequest;
import br.com.fiap.ecoactions_api.dto.response.ColetaResponse;
import br.com.fiap.ecoactions_api.exception.ResourceConflictException;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.model.Coleta;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.repository.ColetaRepository;
import br.com.fiap.ecoactions_api.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColetaService {

    private final ColetaRepository coletaRepository;
    private final PontoColetaService pontoColetaService;
    private final PontoColetaRepository pontoColetaRepository;

    public List<ColetaResponse> listar() {
        return coletaRepository.findAll()
                .stream()
                .map(ColetaResponse::fromEntity)
                .toList();
    }

    public List<ColetaResponse> listarPorPontoColeta(String pontoColetaId) {
        var pontoColeta = pontoColetaService.buscarEntidadePorId(pontoColetaId);

        return coletaRepository.findByPontoColeta(pontoColeta)
                .stream()
                .map(ColetaResponse::fromEntity)
                .toList();
    }

    public ColetaResponse buscarPorId(String id) {
        return ColetaResponse.fromEntity(buscarEntidadePorId(id));
    }

    public ColetaResponse cadastrar(ColetaRequest request) {
        var pontoColeta = pontoColetaService.buscarEntidadePorId(request.pontoColetaId());

        var coleta = new Coleta();
        coleta.setPontoColeta(pontoColeta);
        coleta.setDataColeta(request.dataColeta() != null ? request.dataColeta() : LocalDate.now());
        coleta.setQuantidadeColeta(request.quantidadeColeta());

        atualizarCapacidadeDepoisDaColeta(pontoColeta, request.quantidadeColeta());

        return ColetaResponse.fromEntity(coletaRepository.save(coleta));
    }

    public Coleta buscarEntidadePorId(String id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coleta não encontrada com id: " + id));
    }

    private void atualizarCapacidadeDepoisDaColeta(PontoColeta pontoColeta, Integer quantidadeColeta) {
        var capacidadeAtual = pontoColeta.getCapacidadeAtual() != null ? pontoColeta.getCapacidadeAtual() : 0;

        if (quantidadeColeta > capacidadeAtual) {
            throw new ResourceConflictException("A quantidade coletada não pode superar a quantidade disponível no ponto de coleta.");
        }

        pontoColeta.setCapacidadeAtual(capacidadeAtual - quantidadeColeta);
        pontoColetaRepository.save(pontoColeta);
    }
}
