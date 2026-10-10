package br.com.fiap.ecoactions_api.controller;

import br.com.fiap.ecoactions_api.dto.request.ColetaRequest;
import br.com.fiap.ecoactions_api.dto.response.ColetaResponse;
import br.com.fiap.ecoactions_api.service.ColetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coletas")
@RequiredArgsConstructor
public class ColetaController {

    private final ColetaService coletaService;

    @GetMapping
    public ResponseEntity<List<ColetaResponse>> listar(@RequestParam(required = false) String pontoColetaId) {
        if (pontoColetaId != null) {
            return ResponseEntity.ok(coletaService.listarPorPontoColeta(pontoColetaId));
        }

        return ResponseEntity.ok(coletaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaResponse> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(coletaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ColetaResponse> cadastrar(@RequestBody @Valid ColetaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coletaService.cadastrar(request));
    }
}
