package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.security.SecurityConfig;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@Import(SecurityConfig.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MongoOperations mongoOperations;

    @Test
    void deveRetornarUpSemAutenticacaoQuandoMongoResponde() throws Exception {
        when(mongoOperations.executeCommand(new Document("ping", 1)))
                .thenReturn(new Document("ok", 1.0));

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.application").value("ecoactions-api"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornar503SemExporDetalhesQuandoMongoEstaIndisponivel() throws Exception {
        when(mongoOperations.executeCommand(new Document("ping", 1)))
                .thenThrow(new DataAccessResourceFailureException("Detalhe privado da conexão"));

        mockMvc.perform(get("/api/health"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value("DOWN"))
                .andExpect(jsonPath("$.application").value("ecoactions-api"))
                .andExpect(content().string(not(containsString("Detalhe privado"))));
    }
}
