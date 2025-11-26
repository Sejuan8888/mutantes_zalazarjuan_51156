package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.regex.Pattern;

public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, String[]> {

    private static final int MIN_SIZE = 4;
    private static final Pattern VALID_CHARS_PATTERN = Pattern.compile("^[ATCG]+$");

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // La validación @NotNull y @NotEmpty ya se encarga de estos casos,
        // pero es buena práctica tener una validación defensiva.
        if (dna == null || dna.length == 0) {
            return true; // Dejar que @NotNull/@NotEmpty manejen el error.
        }

        // Validar que la matriz sea cuadrada y de tamaño mínimo
        final int n = dna.length;
        if (n < MIN_SIZE) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("La matriz de ADN debe ser de al menos " + MIN_SIZE + "x" + MIN_SIZE + ".")
                    .addConstraintViolation();
            return false;
        }

        // Validar que todas las filas tengan la longitud correcta y caracteres válidos
        for (String row : dna) {
            if (row == null || row.length() != n) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("La matriz de ADN debe ser cuadrada (NxN). Se encontró una fila de longitud incorrecta.")
                        .addConstraintViolation();
                return false;
            }
            if (!VALID_CHARS_PATTERN.matcher(row).matches()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("La secuencia de ADN contiene caracteres inválidos. Solo se permiten 'A', 'T', 'C', 'G'.")
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
