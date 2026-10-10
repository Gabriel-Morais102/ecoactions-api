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
@Document(collection = "pontos_coleta")
public class PontoColeta {

    @Id
    private String id;

    @Indexed(name = "idx_pontos_residuo")
    @DocumentReference(lazy = false)
    private Residuo residuo;

    @Indexed(name = "idx_pontos_local")
    private String local;

    private Integer capacidadeAtual;

    private Integer capacidadeMaxima;
}
