package br.com.fiap.ecoactions_api.repository;

import br.com.fiap.ecoactions_api.model.PontoColeta;
import br.com.fiap.ecoactions_api.model.Residuo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PontoColetaRepository extends MongoRepository<PontoColeta, String> {

    List<PontoColeta> findByResiduo(Residuo residuo);

    boolean existsByResiduo(Residuo residuo);
}
