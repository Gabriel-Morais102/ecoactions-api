package br.com.fiap.ecoactions_api.repository;

import br.com.fiap.ecoactions_api.model.Coleta;
import br.com.fiap.ecoactions_api.model.Destinacao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DestinacaoRepository extends MongoRepository<Destinacao, String> {

    List<Destinacao> findByColeta(Coleta coleta);
}
