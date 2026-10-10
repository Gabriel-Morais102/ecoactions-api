package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.model.Alerta;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.model.Residuo;
import br.com.fiap.ecoactions_api.repository.AlertaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private PontoColetaService pontoColetaService;

    @InjectMocks
    private AlertaService alertaService;

    @Test
    void deveInformarQuandoPontoAindaTemCapacidade() {
        var ponto = criarPontoColeta(40, 100);
        prepararPersistenciaDoAlerta();
        when(pontoColetaService.buscarEntidadePorId("ponto-1")).thenReturn(ponto);

        var response = alertaService.verificarPontoColeta("ponto-1");

        assertThat(response.id()).isEqualTo("alerta-1");
        assertThat(response.mensagem()).contains("ainda possui capacidade disponível");
        assertThat(response.dataAlerta()).isNotNull();
    }

    @Test
    void deveGerarAlertaQuandoPontoAtingeCapacidadeMaxima() {
        var ponto = criarPontoColeta(100, 100);
        prepararPersistenciaDoAlerta();
        when(pontoColetaService.buscarEntidadePorId("ponto-1")).thenReturn(ponto);

        var response = alertaService.verificarPontoColeta("ponto-1");

        assertThat(response.mensagem())
                .contains("atingiu o limite de capacidade")
                .contains("Vidro");
    }

    private PontoColeta criarPontoColeta(int capacidadeAtual, int capacidadeMaxima) {
        var residuo = new Residuo("residuo-1", "Vidro", capacidadeMaxima);
        return new PontoColeta(
                "ponto-1",
                residuo,
                "Centro Comunitário",
                capacidadeAtual,
                capacidadeMaxima
        );
    }

    private void prepararPersistenciaDoAlerta() {
        when(alertaRepository.save(any(Alerta.class))).thenAnswer(invocation -> {
            var alerta = invocation.getArgument(0, Alerta.class);
            alerta.setId("alerta-1");
            return alerta;
        });
    }
}
