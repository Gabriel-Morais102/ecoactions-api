package br.com.fiap.ecoactions_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alertas")
public class Alerta {

    @Id
    private String id;

    private String mensagem;

    @Indexed(name = "idx_alertas_data", direction = IndexDirection.DESCENDING)
    private LocalDate dataAlerta;
}
