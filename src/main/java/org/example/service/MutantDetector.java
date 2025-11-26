package org.example.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MIN_SEQUENCES_FOR_MUTANT = 2;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Detecta si un humano es mutante basándose en su secuencia de ADN.
     * Un humano es mutante si se encuentran más de una secuencia de 4 letras iguales
     * en direcciones horizontal, vertical u oblicua.
     *
     * @param dna La matriz NxN que representa la secuencia de ADN.
     * @return {@code true} si es mutante, {@code false} en caso contrario.
     */
    public boolean isMutant(String[] dna) {
        if (!isDnaMatrixValid(dna)) {
            return false;
        }

        final int n = dna.length;
        final char[][] matrix = toCharMatrix(dna);
        int sequenceCount = 0;

        // Búsqueda en un solo recorrido (Single Pass)
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {
                // Optimización: Boundary Checking
                // Solo buscar si hay espacio suficiente desde la posición actual.

                // Búsqueda Horizontal (→)
                if (col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 0, 1)) {
                        sequenceCount++;
                    }
                }

                // Búsqueda Vertical (↓)
                if (row <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 0)) {
                        sequenceCount++;
                    }
                }

                // Búsqueda Diagonal Descendente (↘)
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, 1, 1)) {
                        sequenceCount++;
                    }
                }

                // Búsqueda Diagonal Ascendente (↗)
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH) {
                    if (checkSequence(matrix, row, col, -1, 1)) {
                        sequenceCount++;
                    }
                }

                // Optimización Crítica: Early Termination
                // Si ya encontramos más de una secuencia, es mutante. No hay que seguir.
                if (sequenceCount >= MIN_SEQUENCES_FOR_MUTANT) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Valida la matriz de ADN.
     * - No debe ser nula o vacía.
     * - Debe ser cuadrada (NxN).
     * - Debe tener un tamaño mínimo de 4x4.
     * - Solo debe contener caracteres válidos (A, T, C, G).
     */
    private boolean isDnaMatrixValid(String[] dna) {
        if (dna == null || dna.length < SEQUENCE_LENGTH) {
            return false;
        }

        final int n = dna.length;
        for (String row : dna) {
            if (row == null || row.length() != n) {
                return false; // No es cuadrada o una fila es nula
            }
            for (char base : row.toCharArray()) {
                if (!VALID_BASES.contains(base)) {
                    return false; // Carácter inválido
                }
            }
        }
        return true;
    }

    /**
     * Convierte un array de Strings a una matriz de caracteres para acceso más eficiente.
     */
    private char[][] toCharMatrix(String[] dna) {
        final int n = dna.length;
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }
        return matrix;
    }

    /**
     * Verifica una secuencia de 4 letras iguales en una dirección específica.
     *
     * @param matrix La matriz de ADN.
     * @param row    Fila inicial.
     * @param col    Columna inicial.
     * @param dRow   Delta de fila (1 para abajo, -1 para arriba, 0 para horizontal).
     * @param dCol   Delta de columna (1 para derecha, 0 para vertical).
     * @return {@code true} si se encontró una secuencia.
     */
    private boolean checkSequence(char[][] matrix, int row, int col, int dRow, int dCol) {
        final char base = matrix[row][col];
        // Optimización: Comparación Directa
        for (int i = 1; i < SEQUENCE_LENGTH; i++) {
            if (matrix[row + i * dRow][col + i * dCol] != base) {
                return false;
            }
        }
        return true;
    }
}
