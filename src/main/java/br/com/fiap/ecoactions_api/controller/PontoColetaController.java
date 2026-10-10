package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.dto.request.PontoColetaRequest;
import br.com.fiap.ecoactions_api.dto.response.PontoColetaResponse;
import br.com.fiap.ecoactions_api.service.PontoColetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pontos-coleta")
@RequiredArgsConstructor
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;

    @GetMapping
    public ResponseEntity<List<PontoColetaResponse>> listar(@RequestParam(required = false) String residuoId) {
        if (residuoId != null) {
            return ResponseEntity.ok(pontoColetaService.listarPorResiduo(residuoId));
        }

        return ResponseEntity.ok(pontoColetaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(pontoColetaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<PontoColetaResponse> cadastrar(@RequestBody @Valid PontoColetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pontoColetaService.cadastrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> atualizar(
            @PathVariable String id,
            @RequestBody @Valid PontoColetaRequest request
    ) {
        return ResponseEntity.ok(pontoColetaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        pontoColetaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
