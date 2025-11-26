package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta con las estadísticas de las verificaciones de ADN.")
public class StatsResponse {

    @Schema(description = "Cantidad total de ADNs mutantes verificados.", example = "40")
    @JsonProperty("count_mutant_dna")
    private long countMutantDna;

    @Schema(description = "Cantidad total de ADNs humanos verificados.", example = "100")
    @JsonProperty("count_human_dna")
    private long countHumanDna;

    @Schema(description = "Ratio de mutantes sobre humanos (mutantes / humanos).", example = "0.4")
    @JsonProperty("ratio")
    private double ratio;
}
