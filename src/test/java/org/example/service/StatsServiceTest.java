package org.example.service;

import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para StatsService")
class StatsServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;

    @InjectMocks
    private StatsService statsService;

    @Test
    @DisplayName("STATS: Debe calcular estadísticas correctamente con datos")
    void getStats_WhenDataExists_ShouldCalculateCorrectly() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(40L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(100L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(40, stats.getCountMutantDna());
        assertEquals(100, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("STATS: Debe manejar el caso sin humanos (división por cero)")
    void getStats_WhenNoHumans_ShouldHandleRatio() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(10L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(10, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(10.0, stats.getRatio(), 0.001, "El ratio debe ser el número de mutantes si no hay humanos.");
    }

    @Test
    @DisplayName("STATS: Debe devolver ceros cuando no hay datos en la BD")
    void getStats_WhenNoData_ShouldReturnZeros() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(0L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(0L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(0, stats.getCountMutantDna());
        assertEquals(0, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001);
    }
    
    @Test
    @DisplayName("STATS: Debe calcular un ratio de 1.0 con cantidades iguales")
    void getStats_WhenCountsAreEqual_ShouldReturnRatioOne() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(50L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(50L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(50, stats.getCountMutantDna());
        assertEquals(50, stats.getCountHumanDna());
        assertEquals(1.0, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("STATS: Debe calcular un ratio con decimales")
    void getStats_WhenDecimalRatio_ShouldCalculateCorrectly() {
        // Arrange
        when(dnaRecordRepository.countByIsMutant(true)).thenReturn(1L);
        when(dnaRecordRepository.countByIsMutant(false)).thenReturn(3L);

        // Act
        StatsResponse stats = statsService.getStats();

        // Assert
        assertEquals(1, stats.getCountMutantDna());
        assertEquals(3, stats.getCountHumanDna());
        assertEquals(0.333, stats.getRatio(), 0.001);
    }
}
