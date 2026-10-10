package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.ResiduoRequest;
import br.com.fiap.ecoactions_api.dto.response.ResiduoResponse;
import br.com.fiap.ecoactions_api.exception.ResourceConflictException;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.model.Residuo;
import br.com.fiap.ecoactions_api.repository.PontoColetaRepository;
import br.com.fiap.ecoactions_api.repository.ResiduoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResiduoService {

    private final ResiduoRepository residuoRepository;
    private final PontoColetaRepository pontoColetaRepository;

    public List<ResiduoResponse> listar() {
        return residuoRepository.findAll()
                .stream()
                .map(ResiduoResponse::fromEntity)
                .toList();
    }

    public ResiduoResponse buscarPorId(String id) {
        return ResiduoResponse.fromEntity(buscarEntidadePorId(id));
    }

    public ResiduoResponse cadastrar(ResiduoRequest request) {
        var residuo = new Residuo();
        residuo.setTipoResiduo(request.tipoResiduo());
        residuo.setLimiteMaximo(request.limiteMaximo());

        return ResiduoResponse.fromEntity(residuoRepository.save(residuo));
    }

    public ResiduoResponse atualizar(String id, ResiduoRequest request) {
        var residuo = buscarEntidadePorId(id);
        residuo.setTipoResiduo(request.tipoResiduo());
        residuo.setLimiteMaximo(request.limiteMaximo());

        return ResiduoResponse.fromEntity(residuoRepository.save(residuo));
    }

    public void remover(String id) {
        var residuo = buscarEntidadePorId(id);

        if (pontoColetaRepository.existsByResiduo(residuo)) {
            throw new ResourceConflictException("O resíduo está vinculado a pontos de coleta e não pode ser removido.");
        }

        residuoRepository.delete(residuo);
    }

    public Residuo buscarEntidadePorId(String id) {
        return residuoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resíduo não encontrado com id: " + id));
    }
}
