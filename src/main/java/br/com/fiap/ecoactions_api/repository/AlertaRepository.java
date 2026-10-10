package br.com.fiap.ecoactions_api.repository;

import br.com.fiap.ecoactions_api.model.Alerta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlertaRepository extends MongoRepository<Alerta, String> {
}
