package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.PontoColetaRequest;
import br.com.fiap.ecoactions_api.dto.response.PontoColetaResponse;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.repository.PontoColetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PontoColetaService {

    private final PontoColetaRepository pontoColetaRepository;
    private final ResiduoService residuoService;

    public List<PontoColetaResponse> listar() {
        return pontoColetaRepository.findAll()
                .stream()
                .map(PontoColetaResponse::fromEntity)
                .toList();
    }

    public List<PontoColetaResponse> listarPorResiduo(String residuoId) {
        var residuo = residuoService.buscarEntidadePorId(residuoId);

        return pontoColetaRepository.findByResiduo(residuo)
                .stream()
                .map(PontoColetaResponse::fromEntity)
                .toList();
    }

    public PontoColetaResponse buscarPorId(String id) {
        return PontoColetaResponse.fromEntity(buscarEntidadePorId(id));
    }

    public PontoColetaResponse cadastrar(PontoColetaRequest request) {
        var pontoColeta = new PontoColeta();
        preencherDados(pontoColeta, request);

        return PontoColetaResponse.fromEntity(pontoColetaRepository.save(pontoColeta));
    }

    public PontoColetaResponse atualizar(String id, PontoColetaRequest request) {
        var pontoColeta = buscarEntidadePorId(id);
        preencherDados(pontoColeta, request);

        return PontoColetaResponse.fromEntity(pontoColetaRepository.save(pontoColeta));
    }

    public void remover(String id) {
        var pontoColeta = buscarEntidadePorId(id);
        pontoColetaRepository.delete(pontoColeta);
    }

    public PontoColeta buscarEntidadePorId(String id) {
        return pontoColetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta não encontrado com id: " + id));
    }

    private void preencherDados(PontoColeta pontoColeta, PontoColetaRequest request) {
        var residuo = residuoService.buscarEntidadePorId(request.residuoId());

        pontoColeta.setResiduo(residuo);
        pontoColeta.setLocal(request.local());
        pontoColeta.setCapacidadeAtual(request.capacidadeAtual());
        pontoColeta.setCapacidadeMaxima(request.capacidadeMaxima());
    }
}
