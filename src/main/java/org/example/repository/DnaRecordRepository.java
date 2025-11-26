package org.example.repository;

import org.example.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de Spring Data JPA para la entidad DnaRecord.
 * Proporciona métodos CRUD básicos y permite definir consultas personalizadas.
 */
@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    /**
     * Busca un registro de ADN por su hash SHA-256.
     * Se utiliza para verificar si una secuencia de ADN ya ha sido analizada.
     *
     * @param dnaHash El hash del ADN.
     * @return Un {@link Optional} que contiene el {@link DnaRecord} si se encuentra.
     */
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    /**
     * Cuenta la cantidad de registros de ADN que son mutantes o humanos.
     *
     * @param isMutant {@code true} para contar mutantes, {@code false} para contar humanos.
     * @return El conteo de registros.
     */
    long countByIsMutant(boolean isMutant);
}
