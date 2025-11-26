package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.DnaRecord;
import org.example.exception.DnaHashCalculationException;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Analiza una secuencia de ADN, determina si es mutante y persiste el resultado.
     * Implementa una estrategia de caché (write-through) usando la base de datos.
     * Si el ADN ya fue analizado, devuelve el resultado almacenado sin recalcular.
     *
     * @param dna La secuencia de ADN a analizar.
     * @return {@code true} si el ADN es mutante, {@code false} si es humano.
     */
    @Transactional
    public boolean analyzeDna(String[] dna) {
        String dnaHash = calculateDnaHash(dna);

        // 1. Buscar en la "caché" (base de datos)
        Optional<DnaRecord> existingRecord = dnaRecordRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant(); // Devuelve el resultado cacheado
        }

        // 2. Si no está en caché, analizar
        boolean isMutant = mutantDetector.isMutant(dna);

        // 3. Guardar el nuevo resultado en la base de datos
        DnaRecord newRecord = new DnaRecord();
        newRecord.setDnaHash(dnaHash);
        newRecord.setMutant(isMutant);
        dnaRecordRepository.save(newRecord);

        return isMutant;
    }

    /**
     * Calcula el hash SHA-256 de una secuencia de ADN.
     * El hash se utiliza como un identificador único y compacto para evitar
     * almacenar la secuencia completa y para realizar búsquedas rápidas.
     *
     * @param dna La secuencia de ADN.
     * @return El hash SHA-256 en formato hexadecimal.
     * @throws DnaHashCalculationException si el algoritmo SHA-256 no está disponible.
     */
    private String calculateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Concatenar todas las filas para crear una cadena única
            String dnaString = String.join("", dna);
            byte[] hashBytes = digest.digest(dnaString.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            // Esta excepción es muy improbable en un entorno Java estándar.
            throw new DnaHashCalculationException("Error al calcular el hash SHA-256 del ADN.", e);
        }
    }

    /**
     * Convierte un array de bytes a su representación hexadecimal.
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
