package br.com.fiap.ecoactions_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "destinacoes")
public class Destinacao {

    @Id
    private String id;

    @Indexed(name = "idx_destinacoes_coleta")
    @DocumentReference(lazy = false)
    private Coleta coleta;

    @Indexed
    private String tipoDestino;
}
