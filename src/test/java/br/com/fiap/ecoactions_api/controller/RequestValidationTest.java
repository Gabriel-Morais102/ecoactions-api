package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.security.SecurityConfig;
import br.com.fiap.ecoactions_api.service.AlertaService;
import br.com.fiap.ecoactions_api.service.DestinacaoService;
import br.com.fiap.ecoactions_api.service.PontoColetaService;
import br.com.fiap.ecoactions_api.service.ResiduoService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ResiduoController.class, PontoColetaController.class, DestinacaoController.class, AlertaController.class})
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class RequestValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResiduoService residuoService;

    @MockitoBean
    private PontoColetaService pontoColetaService;

    @MockitoBean
    private DestinacaoService destinacaoService;

    @MockitoBean
    private AlertaService alertaService;

    @ParameterizedTest
    @MethodSource("entradasAbaixoDoTamanhoMinimo")
    void deveRejeitarTextoCurtoAntesDeChegarAoBanco(String endpoint, String corpo, String campo) throws Exception {
        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasSize(1)))
                .andExpect(jsonPath("$.details[0]", containsString(campo + ":")));

        verifyNoInteractions(residuoService, pontoColetaService, destinacaoService, alertaService);
    }

    static Stream<Arguments> entradasAbaixoDoTamanhoMinimo() {
        return Stream.of(
                Arguments.of("/api/residuos",
                        "{\"tipoResiduo\":\"A\",\"limiteMaximo\":100}", "tipoResiduo"),
                Arguments.of("/api/pontos-coleta",
                        "{\"residuoId\":\"residuo-1\",\"local\":\"AB\",\"capacidadeAtual\":0,\"capacidadeMaxima\":100}", "local"),
                Arguments.of("/api/destinacoes",
                        "{\"coletaId\":\"coleta-1\",\"tipoDestino\":\"AB\"}", "tipoDestino"),
                Arguments.of("/api/alertas",
                        "{\"mensagem\":\"ABCD\"}", "mensagem")
        );
    }
}
