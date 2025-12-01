package com.dnnr.padrinho_digital_api.repositories.pet;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    /**
     * Busca um Pet pelo ID e já carrega (FETCH) a sua lista de fotos
     * em uma única consulta (LEFT JOIN) para evitar N+1 queries.
     */
    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.photos WHERE p.id = :id")
    Optional<Pet> findByIdWithPhotos(@Param("id") Long id);

    @Query(value = "SELECT p as pet, " +
            "COALESCE(SUM(ch.monthlyAmount), 0.0) as totalCost " +
            "FROM Pet p " +
            "LEFT JOIN p.costs c " +
            "LEFT JOIN c.history ch ON ch.cost = c AND ch.endDate IS NULL " +
            "WHERE p.status = 'APADRINHAVEL'" + "GROUP BY p.id, p.ong.id", // Agrupa pela chave primária (e FKs se necessário)
            countQuery = "SELECT COUNT(p) FROM Pet p")
    Page<PetWithTotalCostProjection> findAllWithTotalCost(Pageable pageable);

    /**
     * Query para ADMIN (Regra 3: Todos os pets, todos os status)
     */
    @Query(value = "SELECT p as pet, " +
            "COALESCE(SUM(ch.monthlyAmount), 0.0) as totalCost " +
            "FROM Pet p " +
            "LEFT JOIN p.costs c ON c.pet = p " +
            "LEFT JOIN c.history ch ON ch.cost = c AND ch.endDate IS NULL " +
            "GROUP BY p.id, p.ong.id", // Agrupa pela chave primária
            countQuery = "SELECT COUNT(p) FROM Pet p")
    Page<PetWithTotalCostProjection> findAllWithTotalCost_Admin(Pageable pageable);

    /**
     * Query para VOLUNTARIO / GERENTE (Regra 2: Todos os pets da ONG, todos os status)
     */
    @Query(value = "SELECT p as pet, " +
            "COALESCE(SUM(ch.monthlyAmount), 0.0) as totalCost " +
            "FROM Pet p " +
            "LEFT JOIN p.costs c ON c.pet = p " +
            "LEFT JOIN c.history ch ON ch.cost = c AND ch.endDate IS NULL " +
            "WHERE p.ong.id = :ongId " + // Filtro da Regra 2
            "GROUP BY p.id, p.ong.id",
            countQuery = "SELECT COUNT(p) FROM Pet p WHERE p.ong.id = :ongId")
    Page<PetWithTotalCostProjection> findAllWithTotalCost_ByOng(@Param("ongId") Long ongId, Pageable pageable);

    /**
     * Query para PADRINHO (Regra 1: Status 'APADRINHAVEL' e que o apadrinhamento não está ATIVO)
     */
    @Query(value = "SELECT p as pet, " +
            "COALESCE(SUM(ch.monthlyAmount), 0.0) as totalCost " +
            "FROM Pet p " +
            "LEFT JOIN p.costs c " +
            "LEFT JOIN c.history ch WITH ch.endDate IS NULL " +
            "WHERE p.status = 'APADRINHAVEL' " +
            "AND NOT EXISTS (SELECT 1 FROM SponsorshipHistory sh " +
            "WHERE sh.sponsorship.pet.id = p.id " +
            "AND sh.sponsorship.godfather.id = :godfatherId " +
            "AND sh.endDate IS NULL) " +
            "GROUP BY p",
            countQuery = "SELECT COUNT(p) FROM Pet p " +
                    "WHERE p.status = 'APADRINHAVEL' " +
                    "AND NOT EXISTS (SELECT 1 FROM SponsorshipHistory sh " +
                    "WHERE sh.sponsorship.pet.id = p.id " +
                    "AND sh.sponsorship.godfather.id = :godfatherId " +
                    "AND sh.endDate IS NULL)")
    Page<PetWithTotalCostProjection> findAllWithTotalCost_ForPadrinho(@Param("godfatherId") Long godfatherId, Pageable pageable);

    long countByOngId(Long ongId);

    @Query("SELECT COUNT(p) FROM Pet p WHERE p.ong.id = :ongId AND p.status = 'APADRINHADO'")
    long countSponsoredByOngId(@Param("ongId") Long ongId);

    @Query("SELECT COUNT(p) FROM Pet p WHERE p.ong.id = :ongId AND p.status = 'APADRINHAVEL'")
    long countAvailableByOngId(@Param("ongId") Long ongId);

    // Animais criados nos últimos 7 dias
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.ong.id = :ongId AND p.createdAt >= :date")
    long countNewPetsSince(@Param("ongId") Long ongId, @Param("date") LocalDateTime date);

    // Lista de animais recentes
    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.photos WHERE p.ong.id = :ongId ORDER BY p.createdAt DESC")
    List<Pet> findRecentByOngId(@Param("ongId") Long ongId, Pageable pageable);

    List<Pet> findAllByOngId(Long ongId);
}
