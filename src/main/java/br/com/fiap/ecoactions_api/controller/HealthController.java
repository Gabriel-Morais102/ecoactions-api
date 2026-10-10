package br.com.fiap.ecoactions_api.controller;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final MongoOperations mongoOperations;

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        try {
            mongoOperations.executeCommand(new Document("ping", 1));
            return ResponseEntity.ok(response("UP"));
        } catch (DataAccessException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response("DOWN"));
        }
    }

    private Map<String, Object> response(String status) {
        return Map.of(
                "status", status,
                "application", "ecoactions-api",
                "timestamp", OffsetDateTime.now()
        );
    }
}
