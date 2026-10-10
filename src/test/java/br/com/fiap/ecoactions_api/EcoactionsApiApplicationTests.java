package br.com.fiap.ecoactions_api;

import br.com.fiap.ecoactions_api.repository.AlertaRepository;
import br.com.fiap.ecoactions_api.repository.ColetaRepository;
import br.com.fiap.ecoactions_api.repository.DestinacaoRepository;
import br.com.fiap.ecoactions_api.repository.PontoColetaRepository;
import br.com.fiap.ecoactions_api.repository.ResiduoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude="
				+ "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,"
				+ "org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration"
})
class EcoactionsApiApplicationTests {

	@MockitoBean
	private ResiduoRepository residuoRepository;

	@MockitoBean
	private PontoColetaRepository pontoColetaRepository;

	@MockitoBean
	private ColetaRepository coletaRepository;

	@MockitoBean
	private DestinacaoRepository destinacaoRepository;

	@MockitoBean
	private AlertaRepository alertaRepository;

	@Test
	void contextLoads() {
	}

}
