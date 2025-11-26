package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.DnaRequest;
import org.example.dto.StatsResponse;
import org.example.service.MutantService;
import org.example.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "API de Detección de Mutantes", description = "Endpoints para analizar secuencias de ADN y ver estadísticas.")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    @PostMapping("/mutant")
    @Operation(summary = "Verifica si una secuencia de ADN pertenece a un mutante.",
            description = "Recibe una secuencia de ADN y la analiza. Si el ADN corresponde a un mutante, devuelve 200 OK. Si es humano, devuelve 403 Forbidden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "El ADN analizado CORRESPONDE a un mutante.", content = @Content),
            @ApiResponse(responseCode = "403", description = "El ADN analizado NO CORRESPONDE a un mutante (es humano).", content = @Content),
            @ApiResponse(responseCode = "400", description = "La petición es inválida (e.g., ADN no es NxN, contiene caracteres inválidos).", content = @Content)
    })
    public ResponseEntity<Void> checkMutant(@Validated @RequestBody DnaRequest request) {
        boolean isMutant = mutantService.analyzeDna(request.getDna());

        if (isMutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtiene las estadísticas de las verificaciones de ADN.",
            description = "Devuelve un JSON con la cantidad de ADNs mutantes, la cantidad de ADNs humanos y el ratio de mutantes sobre humanos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente.")
    })
    public ResponseEntity<StatsResponse> getStats() {
        StatsResponse stats = statsService.getStats();
        return ResponseEntity.ok(stats);
    }
}
