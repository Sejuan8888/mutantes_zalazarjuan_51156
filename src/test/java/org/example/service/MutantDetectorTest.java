package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Pruebas Unitarias para MutantDetector")
class MutantDetectorTest {

    private MutantDetector mutantDetector;

    @BeforeEach
    void setUp() {
        mutantDetector = new MutantDetector();
    }

    // --- Casos de Mutantes (Debe devolver true) ---

    @Test
    @DisplayName("MUTANTE: 2 secuencias horizontales")
    void isMutant_WhenTwoHorizontalSequences_ShouldReturnTrue() {
        String[] dna = {
                "AAAA",
                "CCCC",
                "TTAT",
                "AGAC"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar 2 secuencias horizontales.");
    }

    @Test
    @DisplayName("MUTANTE: 2 secuencias verticales")
    void isMutant_WhenTwoVerticalSequences_ShouldReturnTrue() {
        String[] dna = {
                "ATGC",
                "ATGC",
                "ATGC",
                "ATGC"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar 2 secuencias verticales.");
    }

    @Test
    @DisplayName("MUTANTE: 2 secuencias diagonales descendentes")
    void isMutant_WhenTwoDiagonalDescendingSequences_ShouldReturnTrue() {
        String[] dna = {
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar una secuencia horizontal y una diagonal.");
    }
    
    @Test
    @DisplayName("MUTANTE: 2 secuencias diagonales ascendentes")
    void isMutant_WhenTwoDiagonalAscendingSequences_ShouldReturnTrue() {
        String[] dna = {
                "GCGT",
                "GCGA",
                "GCAG",
                "GACG"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar 2 secuencias diagonales ascendentes.");
    }

    @Test
    @DisplayName("MUTANTE: Mezcla de secuencias (horizontal y vertical)")
    void isMutant_WhenMixedSequences_ShouldReturnTrue() {
        String[] dna = {
                "AAAA",
                "C   ",
                "C   ",
                "C   "
        };
        // Para que la matriz sea válida
        dna = new String[] {
                "AAAA",
                "CTTT",
                "CTTT",
                "CTTT"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe detectar una secuencia horizontal y una vertical.");
    }
    
    @Test
    @DisplayName("MUTANTE: Matriz grande 10x10 con secuencias")
    void isMutant_WhenLargeMatrixWithSequences_ShouldReturnTrue() {
        String[] dna = {
            "ATGCGAATGC",
            "CAGTGCCAGT",
            "TTATGTTTAT",
            "AGAAGGATAA",
            "CCCCTACCCC", // 2 horizontales
            "TCACTGTCAC",
            "ATGCGAATGC",
            "CAGTGCCAGT",
            "TTATGTTTAT",
            "AGAAGGATAA"
        };
        assertTrue(mutantDetector.isMutant(dna), "Debe funcionar en matrices grandes.");
    }

    // --- Casos de Humanos (Debe devolver false) ---

    @Test
    @DisplayName("HUMANO: 1 sola secuencia horizontal")
    void isNotMutant_WhenOnlyOneHorizontalSequence_ShouldReturnFalse() {
        String[] dna = {
                "AAAA",
                "CTGT",
                "TTAT",
                "AGAC"
        };
        assertFalse(mutantDetector.isMutant(dna), "No debe ser mutante con una sola secuencia.");
    }

    @Test
    @DisplayName("HUMANO: Sin secuencias de 4 letras")
    void isNotMutant_WhenNoSequences_ShouldReturnFalse() {
        String[] dna = {
                "ATCG",
                "TCGA",
                "CGAT",
                "GATC"
        };
        assertFalse(mutantDetector.isMutant(dna), "No debe ser mutante si no hay secuencias.");
    }

    @Test
    @DisplayName("HUMANO: Secuencias de 3 letras no cuentan")
    void isNotMutant_WhenOnlySequencesOf3_ShouldReturnFalse() {
        String[] dna = {
                "AAAT",
                "CCCG",
                "TTTA",
                "GGGC"
        };
        assertFalse(mutantDetector.isMutant(dna), "Las secuencias de 3 letras no deben contar.");
    }

    // --- Casos de Validación (Debe devolver false) ---

    @ParameterizedTest
    @MethodSource("provideInvalidDnaMatrices")
    @DisplayName("VALIDACIÓN: Rechazar matrices de ADN inválidas")
    void isMutant_WhenDnaIsInvalid_ShouldReturnFalse(String[] invalidDna, String description) {
        assertFalse(mutantDetector.isMutant(invalidDna), description);
    }

    private static Stream<Object[]> provideInvalidDnaMatrices() {
        return Stream.of(
            new Object[]{null, "El ADN nulo debe devolver false."},
            new Object[]{new String[]{}, "El ADN vacío debe devolver false."},
            new Object[]{new String[]{"ATGC", "CG", "ATGC", "CGAT"}, "Una matriz no cuadrada debe devolver false."},
            new Object[]{new String[]{"ATGC", "CGAT", "GCTA", null}, "Una matriz con una fila nula debe devolver false."},
            new Object[]{new String[]{"ATGC", "CGBt", "ATGC", "CGAT"}, "Una matriz con caracteres inválidos (minúscula) debe devolver false."},
            new Object[]{new String[]{"ATGC", "CGBZ", "ATGC", "CGAT"}, "Una matriz con caracteres inválidos (Z) debe devolver false."},
            new Object[]{new String[]{"AG", "TC"}, "Una matriz de 2x2 (menor que 4x4) debe devolver false."}
        );
    }
}
