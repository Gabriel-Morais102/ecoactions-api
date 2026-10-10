package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.dto.request.DestinacaoRequest;
import br.com.fiap.ecoactions_api.dto.response.DestinacaoResponse;
import br.com.fiap.ecoactions_api.service.DestinacaoService;
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
@RequestMapping("/api/destinacoes")
@RequiredArgsConstructor
public class DestinacaoController {

    private final DestinacaoService destinacaoService;

    @GetMapping
    public ResponseEntity<List<DestinacaoResponse>> listar(@RequestParam(required = false) String coletaId) {
        if (coletaId != null) {
            return ResponseEntity.ok(destinacaoService.listarPorColeta(coletaId));
        }

        return ResponseEntity.ok(destinacaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DestinacaoResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(destinacaoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<DestinacaoResponse> cadastrar(@RequestBody @Valid DestinacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(destinacaoService.cadastrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DestinacaoResponse> atualizar(
            @PathVariable String id,
            @RequestBody @Valid DestinacaoRequest request
    ) {
        return ResponseEntity.ok(destinacaoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        destinacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
