package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.exception.ResourceConflictException;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.model.Residuo;
import br.com.fiap.ecoactions_api.repository.ColetaRepository;
import br.com.fiap.ecoactions_api.repository.PontoColetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PontoColetaServiceTest {

    @Mock
    private PontoColetaRepository pontoColetaRepository;

    @Mock
    private ColetaRepository coletaRepository;

    @Mock
    private ResiduoService residuoService;

    @InjectMocks
    private PontoColetaService pontoColetaService;

    @Test
    void deveImpedirExclusaoDePontoComColetas() {
        var ponto = criarPonto();
        when(pontoColetaRepository.findById("ponto-1")).thenReturn(Optional.of(ponto));
        when(coletaRepository.existsByPontoColeta(ponto)).thenReturn(true);

        assertThatThrownBy(() -> pontoColetaService.remover("ponto-1"))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("coletas registradas");

        verify(pontoColetaRepository, never()).delete(any(PontoColeta.class));
    }

    @Test
    void deveRemoverPontoSemColetas() {
        var ponto = criarPonto();
        when(pontoColetaRepository.findById("ponto-1")).thenReturn(Optional.of(ponto));
        when(coletaRepository.existsByPontoColeta(ponto)).thenReturn(false);

        pontoColetaService.remover("ponto-1");

        verify(pontoColetaRepository).delete(ponto);
    }

    private PontoColeta criarPonto() {
        var residuo = new Residuo("residuo-1", "Papel", 100);
        return new PontoColeta("ponto-1", residuo, "Escola Municipal", 20, 100);
    }
}
