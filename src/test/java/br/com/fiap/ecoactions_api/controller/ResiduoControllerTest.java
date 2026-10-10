package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.dto.request.ResiduoRequest;
import br.com.fiap.ecoactions_api.dto.response.ResiduoResponse;
import br.com.fiap.ecoactions_api.exception.ResourceConflictException;
import br.com.fiap.ecoactions_api.exception.ResourceNotFoundException;
import br.com.fiap.ecoactions_api.security.SecurityConfig;
import br.com.fiap.ecoactions_api.service.ResiduoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ResiduoController.class, properties = {
        "app.security.admin.username=admin",
        "app.security.admin.password=senha-admin-teste",
        "app.security.user.username=user",
        "app.security.user.password=senha-user-teste"
})
@Import(SecurityConfig.class)
class ResiduoControllerTest {

    private static final String RESIDUO_VALIDO = """
            {"tipoResiduo":"Papel","limiteMaximo":100}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResiduoService residuoService;

    @Test
    void deveExigirAutenticacaoParaConsultarResiduos() throws Exception {
        mockMvc.perform(get("/api/residuos"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(residuoService);
    }

    @Test
    void deveRejeitarSenhaIncorreta() throws Exception {
        mockMvc.perform(get("/api/residuos").with(httpBasic("admin", "senha-incorreta")))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(residuoService);
    }

    @Test
    void devePermitirConsultaAoUsuario() throws Exception {
        when(residuoService.listar()).thenReturn(List.of(new ResiduoResponse("residuo-1", "Papel", 100)));

        mockMvc.perform(get("/api/residuos").with(httpBasic("user", "senha-user-teste")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipoResiduo").value("Papel"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "PUT", "DELETE"})
    void deveProibirAlteracoesPeloUsuario(String metodo) throws Exception {
        var path = metodo.equals("POST") ? "/api/residuos" : "/api/residuos/residuo-1";

        mockMvc.perform(request(HttpMethod.valueOf(metodo), path)
                        .with(httpBasic("user", "senha-user-teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESIDUO_VALIDO))
                .andExpect(status().isForbidden());

        verifyNoInteractions(residuoService);
    }

    @Test
    void devePermitirCadastroPeloAdministrador() throws Exception {
        when(residuoService.cadastrar(any(ResiduoRequest.class)))
                .thenReturn(new ResiduoResponse("residuo-1", "Papel", 100));

        mockMvc.perform(post("/api/residuos")
                        .with(httpBasic("admin", "senha-admin-teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESIDUO_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("residuo-1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{", "{\"tipoResiduo\":\"Papel\",\"limiteMaximo\":\"inválido\"}"})
    void deveRetornar400ParaJsonInvalidoOuTipoIncompativel(String corpo) throws Exception {
        mockMvc.perform(post("/api/residuos")
                        .with(httpBasic("admin", "senha-admin-teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("O corpo da requisição contém JSON inválido ou valores incompatíveis."));

        verifyNoInteractions(residuoService);
    }

    @Test
    void deveRetornar400ParaCamposInvalidos() throws Exception {
        mockMvc.perform(post("/api/residuos")
                        .with(httpBasic("admin", "senha-admin-teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipoResiduo\":\"\",\"limiteMaximo\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", hasSize(2)));

        verifyNoInteractions(residuoService);
    }

    @Test
    void deveRetornar404ParaResiduoInexistente() throws Exception {
        when(residuoService.buscarPorId("inexistente"))
                .thenThrow(new ResourceNotFoundException("Resíduo não encontrado."));

        mockMvc.perform(get("/api/residuos/inexistente").with(httpBasic("user", "senha-user-teste")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deveRetornar409ParaTipoDeResiduoDuplicado() throws Exception {
        when(residuoService.cadastrar(any(ResiduoRequest.class)))
                .thenThrow(new DuplicateKeyException("Detalhe privado do índice MongoDB"));

        mockMvc.perform(post("/api/residuos")
                        .with(httpBasic("admin", "senha-admin-teste"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RESIDUO_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(content().string(not(containsString("Detalhe privado"))));
    }

    @Test
    void deveRetornar409AoExcluirResiduoVinculado() throws Exception {
        doThrow(new ResourceConflictException("O resíduo está vinculado a pontos de coleta."))
                .when(residuoService).remover("residuo-1");

        mockMvc.perform(delete("/api/residuos/residuo-1").with(httpBasic("admin", "senha-admin-teste")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("O resíduo está vinculado a pontos de coleta."));
    }

    @Test
    void deveRetornar500SemExporDetalhesDeErroInesperado() throws Exception {
        when(residuoService.listar()).thenThrow(new IllegalStateException("Detalhe privado da falha"));

        mockMvc.perform(get("/api/residuos").with(httpBasic("user", "senha-user-teste")))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno na aplicação."))
                .andExpect(content().string(not(containsString("Detalhe privado"))));
    }
}
