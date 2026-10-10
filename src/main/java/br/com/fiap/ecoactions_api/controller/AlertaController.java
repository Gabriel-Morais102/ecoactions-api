package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.dto.request.AlertaRequest;
import br.com.fiap.ecoactions_api.dto.response.AlertaResponse;
import br.com.fiap.ecoactions_api.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping
    public ResponseEntity<List<AlertaResponse>> listar() {
        return ResponseEntity.ok(alertaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(alertaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlertaResponse> cadastrar(@RequestBody @Valid AlertaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaService.cadastrar(request));
    }

    @PostMapping("/verificar-ponto/{pontoColetaId}")
    public ResponseEntity<AlertaResponse> verificarPontoColeta(@PathVariable String pontoColetaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertaService.verificarPontoColeta(pontoColetaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        alertaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
