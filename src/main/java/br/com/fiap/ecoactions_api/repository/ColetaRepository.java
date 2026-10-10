package br.com.fiap.ecoactions_api.repository;

import br.com.fiap.ecoactions_api.model.Coleta;
import br.com.fiap.ecoactions_api.model.PontoColeta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ColetaRepository extends MongoRepository<Coleta, String> {

    List<Coleta> findByPontoColeta(PontoColeta pontoColeta);

    boolean existsByPontoColeta(PontoColeta pontoColeta);
}
