package br.com.fiap.ecoactions_api.service;

import br.com.fiap.ecoactions_api.dto.request.ResiduoRequest;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.model.Residuo;
import br.com.fiap.ecoactions_api.repository.ResiduoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResiduoServiceTest {

    @Mock
    private ResiduoRepository residuoRepository;

    @InjectMocks
    private ResiduoService residuoService;

    @Test
    void deveCadastrarResiduo() {
        var request = new ResiduoRequest("Plástico", 100);
        when(residuoRepository.save(any(Residuo.class))).thenAnswer(invocation -> {
            var residuo = invocation.getArgument(0, Residuo.class);
            residuo.setId("residuo-1");
            return residuo;
        });

        var response = residuoService.cadastrar(request);

        assertThat(response.id()).isEqualTo("residuo-1");
        assertThat(response.tipoResiduo()).isEqualTo("Plástico");
        assertThat(response.limiteMaximo()).isEqualTo(100);
        verify(residuoRepository).save(any(Residuo.class));
    }

    @Test
    void deveListarResiduosCadastrados() {
        when(residuoRepository.findAll()).thenReturn(List.of(
                new Residuo("1", "Papel", 80),
                new Residuo("2", "Vidro", 120)
        ));

        var response = residuoService.listar();

        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting("tipoResiduo")
                .containsExactly("Papel", "Vidro");
    }

    @Test
    void deveLancarExcecaoQuandoResiduoNaoExiste() {
        when(residuoRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> residuoService.buscarPorId("inexistente"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("inexistente");
    }
}
