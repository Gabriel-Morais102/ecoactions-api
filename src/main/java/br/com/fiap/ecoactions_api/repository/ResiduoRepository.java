package br.com.fiap.ecoactions_api.repository;

import br.com.fiap.ecoactions_api.model.Residuo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResiduoRepository extends MongoRepository<Residuo, String> {
}
