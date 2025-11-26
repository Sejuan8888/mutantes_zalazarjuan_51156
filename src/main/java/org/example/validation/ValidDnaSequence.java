package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación de validación personalizada para secuencias de ADN.
 * Verifica que el array de Strings representa una matriz NxN válida con
 * caracteres permitidos (A, T, C, G) y un tamaño mínimo.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDnaSequenceValidator.class)
public @interface ValidDnaSequence {
    String message() default "La secuencia de ADN es inválida. Debe ser una matriz NxN (mínimo 4x4) con solo los caracteres A, T, C, G.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
