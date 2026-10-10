package br.com.fiap.ecoactions_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "residuos")
public class Residuo {

    @Id
    private String id;

    @Indexed(name = "uk_residuos_tipo", unique = true)
    private String tipoResiduo;

    private Integer limiteMaximo;
}
