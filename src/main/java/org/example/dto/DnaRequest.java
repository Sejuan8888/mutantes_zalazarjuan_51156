package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.validation.ValidDnaSequence;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar una secuencia de ADN.")
public class DnaRequest {

    @Schema(
            description = "Secuencia de ADN representada como un array de Strings (matriz NxN).",
            example = "[\"ATGCGA\",\"CAGTGC\",\"TTATGT\",\"AGAAGG\",\"CCCCTA\",\"TCACTG\"]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull(message = "El campo 'dna' no puede ser nulo.")
    @NotEmpty(message = "El campo 'dna' no puede estar vacío.")
    @ValidDnaSequence
    private String[] dna;
}
