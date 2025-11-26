package org.example.exception;

/**
 * Excepción personalizada que se lanza si ocurre un error durante
 * el cálculo del hash SHA-256 de una secuencia de ADN.
 * Esto podría ocurrir si el algoritmo de hashing especificado no está disponible.
 */
public class DnaHashCalculationException extends RuntimeException {
    public DnaHashCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
