package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Calcula las estadísticas de verificaciones de ADN.
     * Cuenta el número de mutantes y humanos y calcula el ratio.
     *
     * @return Un objeto {@link StatsResponse} con las estadísticas.
     */
    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        long mutantCount = dnaRecordRepository.countByIsMutant(true);
        long humanCount = dnaRecordRepository.countByIsMutant(false);

        double ratio = 0.0;
        if (humanCount > 0) {
            ratio = (double) mutantCount / humanCount;
        } else if (mutantCount > 0) {
            // Caso especial: si hay mutantes pero no humanos, el ratio es el número de mutantes.
            ratio = mutantCount;
        }

        return new StatsResponse(mutantCount, humanCount, ratio);
    }
}
