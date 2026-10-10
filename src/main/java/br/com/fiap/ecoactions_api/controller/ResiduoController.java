package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.dto.request.ResiduoRequest;
import br.com.fiap.ecoactions_api.dto.response.ResiduoResponse;
import br.com.fiap.ecoactions_api.service.ResiduoService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/residuos")
@RequiredArgsConstructor
public class ResiduoController {

    private final ResiduoService residuoService;

    @GetMapping
    public ResponseEntity<List<ResiduoResponse>> listar() {
        return ResponseEntity.ok(residuoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResiduoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(residuoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ResiduoResponse> cadastrar(@RequestBody @Valid ResiduoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(residuoService.cadastrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResiduoResponse> atualizar(
            @PathVariable String id,
            @RequestBody @Valid ResiduoRequest request
    ) {
        return ResponseEntity.ok(residuoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        residuoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
