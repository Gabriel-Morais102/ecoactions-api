package br.com.fiap.ecoactions_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coletas")
@CompoundIndex(
        name = "idx_coletas_ponto_data",
        def = "{'pontoColeta': 1, 'dataColeta': -1}"
)
public class Coleta {

    @Id
    private String id;

    @DocumentReference(lazy = false)
    private PontoColeta pontoColeta;

    private LocalDate dataColeta;

    private Integer quantidadeColeta;
}
