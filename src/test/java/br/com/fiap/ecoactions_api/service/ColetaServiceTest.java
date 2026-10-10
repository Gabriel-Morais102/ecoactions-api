package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.ColetaRequest;
import br.com.fiap.ecoactions_api.exception.ResourceConflictException;
import br.com.fiap.ecoactions_api.model.Coleta;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.model.Residuo;
import br.com.fiap.ecoactions_api.repository.ColetaRepository;
import br.com.fiap.ecoactions_api.repository.PontoColetaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColetaServiceTest {

    @Mock
    private ColetaRepository coletaRepository;

    @Mock
    private PontoColetaService pontoColetaService;

    @Mock
    private PontoColetaRepository pontoColetaRepository;

    @InjectMocks
    private ColetaService coletaService;

    @Test
    void deveRegistrarColetaEAtualizarCapacidadeDoPonto() {
        var residuo = new Residuo("residuo-1", "Plástico", 100);
        var ponto = new PontoColeta("ponto-1", residuo, "Parque Central", 80, 100);
        var dataColeta = LocalDate.of(2026, 10, 10);
        var request = new ColetaRequest("ponto-1", dataColeta, 30);

        when(pontoColetaService.buscarEntidadePorId("ponto-1")).thenReturn(ponto);
        when(coletaRepository.save(any(Coleta.class))).thenAnswer(invocation -> {
            var coleta = invocation.getArgument(0, Coleta.class);
            coleta.setId("coleta-1");
            return coleta;
        });

        var response = coletaService.cadastrar(request);

        assertThat(response.id()).isEqualTo("coleta-1");
        assertThat(response.quantidadeColeta()).isEqualTo(30);
        assertThat(response.dataColeta()).isEqualTo(dataColeta);
        assertThat(ponto.getCapacidadeAtual()).isEqualTo(50);
        verify(pontoColetaRepository).save(ponto);
    }

    @Test
    void deveRejeitarColetaAcimaDoSaldoSemAlterarDados() {
        var residuo = new Residuo("residuo-1", "Papel", 100);
        var ponto = new PontoColeta("ponto-1", residuo, "Escola Municipal", 20, 100);
        var request = new ColetaRequest("ponto-1", LocalDate.now(), 50);

        when(pontoColetaService.buscarEntidadePorId("ponto-1")).thenReturn(ponto);

        assertThatThrownBy(() -> coletaService.cadastrar(request))
                .isInstanceOf(ResourceConflictException.class)
                .hasMessageContaining("quantidade disponível");

        assertThat(ponto.getCapacidadeAtual()).isEqualTo(20);
        verifyNoInteractions(coletaRepository, pontoColetaRepository);
    }

    @Test
    void devePermitirColetaDeTodoOSaldoDisponivel() {
        var residuo = new Residuo("residuo-1", "Papel", 100);
        var ponto = new PontoColeta("ponto-1", residuo, "Escola Municipal", 20, 100);
        var request = new ColetaRequest("ponto-1", LocalDate.now(), 20);

        when(pontoColetaService.buscarEntidadePorId("ponto-1")).thenReturn(ponto);
        when(coletaRepository.save(any(Coleta.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = coletaService.cadastrar(request);

        assertThat(ponto.getCapacidadeAtual()).isZero();
        assertThat(response.quantidadeColeta()).isEqualTo(20);
        verify(pontoColetaRepository).save(ponto);
    }
}
