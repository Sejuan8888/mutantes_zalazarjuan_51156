package org.example.service;

import org.example.entity.DnaRecord;
import org.example.repository.DnaRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias para MutantService")
class MutantServiceTest {

    @Mock
    private DnaRecordRepository dnaRecordRepository;
    @Mock
    private MutantDetector mutantDetector;

    @InjectMocks
    private MutantService mutantService;

    private final String[] mutantDna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
    private final String[] humanDna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};

    @Test
    @DisplayName("SERVICE: Analiza ADN mutante nuevo y lo guarda")
    void analyzeDna_WhenMutantAndNew_ShouldAnalyzeAndSave() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(mutantDna)).thenReturn(true);
        ArgumentCaptor<DnaRecord> recordCaptor = ArgumentCaptor.forClass(DnaRecord.class);

        // Act
        boolean result = mutantService.analyzeDna(mutantDna);

        // Assert
        assertTrue(result, "Debería devolver true para ADN mutante.");
        verify(dnaRecordRepository, times(1)).findByDnaHash(anyString());
        verify(mutantDetector, times(1)).isMutant(mutantDna);
        verify(dnaRecordRepository, times(1)).save(recordCaptor.capture());

        DnaRecord savedRecord = recordCaptor.getValue();
        assertTrue(savedRecord.isMutant());
        assertNotNull(savedRecord.getDnaHash());
        assertEquals(64, savedRecord.getDnaHash().length());
    }

    @Test
    @DisplayName("SERVICE: Analiza ADN humano nuevo y lo guarda")
    void analyzeDna_WhenHumanAndNew_ShouldAnalyzeAndSave() {
        // Arrange
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(mutantDetector.isMutant(humanDna)).thenReturn(false);
        ArgumentCaptor<DnaRecord> recordCaptor = ArgumentCaptor.forClass(DnaRecord.class);

        // Act
        boolean result = mutantService.analyzeDna(humanDna);

        // Assert
        assertFalse(result, "Debería devolver false para ADN humano.");
        verify(dnaRecordRepository, times(1)).save(recordCaptor.capture());
        assertFalse(recordCaptor.getValue().isMutant());
    }

    @Test
    @DisplayName("CACHE: Devuelve resultado de mutante cacheado sin analizar de nuevo")
    void analyzeDna_WhenMutantIsCached_ShouldReturnCachedResult() {
        // Arrange
        DnaRecord cachedRecord = new DnaRecord();
        cachedRecord.setMutant(true);
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        // Act
        boolean result = mutantService.analyzeDna(mutantDna);

        // Assert
        assertTrue(result, "Debería devolver el resultado cacheado 'true'.");
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("CACHE: Devuelve resultado de humano cacheado sin analizar de nuevo")
    void analyzeDna_WhenHumanIsCached_ShouldReturnCachedResult() {
        // Arrange
        DnaRecord cachedRecord = new DnaRecord();
        cachedRecord.setMutant(false);
        when(dnaRecordRepository.findByDnaHash(anyString())).thenReturn(Optional.of(cachedRecord));

        // Act
        boolean result = mutantService.analyzeDna(humanDna);

        // Assert
        assertFalse(result, "Debería devolver el resultado cacheado 'false'.");
        verify(mutantDetector, never()).isMutant(any());
        verify(dnaRecordRepository, never()).save(any());
    }
}
