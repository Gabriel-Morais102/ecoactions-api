package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.DestinacaoRequest;
import br.com.fiap.ecoactions_api.dto.response.DestinacaoResponse;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.model.Destinacao;
import br.com.fiap.ecoactions_api.repository.DestinacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DestinacaoService {

    private final DestinacaoRepository destinacaoRepository;
    private final ColetaService coletaService;

    public List<DestinacaoResponse> listar() {
        return destinacaoRepository.findAll()
                .stream()
                .map(DestinacaoResponse::fromEntity)
                .toList();
    }

    public List<DestinacaoResponse> listarPorColeta(String coletaId) {
        var coleta = coletaService.buscarEntidadePorId(coletaId);

        return destinacaoRepository.findByColeta(coleta)
                .stream()
                .map(DestinacaoResponse::fromEntity)
                .toList();
    }

    public DestinacaoResponse buscarPorId(String id) {
        return DestinacaoResponse.fromEntity(buscarEntidadePorId(id));
    }

    public DestinacaoResponse cadastrar(DestinacaoRequest request) {
        var coleta = coletaService.buscarEntidadePorId(request.coletaId());

        var destinacao = new Destinacao();
        destinacao.setColeta(coleta);
        destinacao.setTipoDestino(request.tipoDestino());

        return DestinacaoResponse.fromEntity(destinacaoRepository.save(destinacao));
    }

    public DestinacaoResponse atualizar(String id, DestinacaoRequest request) {
        var coleta = coletaService.buscarEntidadePorId(request.coletaId());
        var destinacao = buscarEntidadePorId(id);

        destinacao.setColeta(coleta);
        destinacao.setTipoDestino(request.tipoDestino());

        return DestinacaoResponse.fromEntity(destinacaoRepository.save(destinacao));
    }

    public void remover(String id) {
        var destinacao = buscarEntidadePorId(id);
        destinacaoRepository.delete(destinacao);
    }

    private Destinacao buscarEntidadePorId(String id) {
        return destinacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destinação não encontrada com id: " + id));
    }
}
