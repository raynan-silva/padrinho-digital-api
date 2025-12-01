package com.dnnr.padrinho_digital_api.repositories.sponsorship;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.sponsorship.Sponsorship;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {

    // Usado para verificar duplicidade
    Optional<Sponsorship> findByGodfatherAndPet(Godfather godfather, Pet pet);

    // Para listagem do Padrinho
    Page<Sponsorship> findByGodfather(Godfather godfather, Pageable pageable);

    // Para listagem da ONG
    Page<Sponsorship> findByPet_Ong(Ong ong, Pageable pageable);

    // Para listagem do Admin por Pet
    Page<Sponsorship> findByPet(Pet pet, Pageable pageable);

    // Para GET by ID (trazendo todo o histórico)
    @Query("SELECT s FROM Sponsorship s LEFT JOIN FETCH s.history WHERE s.id = :id")
    Optional<Sponsorship> findByIdWithHistory(@Param("id") Long id);

    // 1. Contar apadrinhamentos ATIVOS
    @Query("SELECT COUNT(s) FROM Sponsorship s " +
            "JOIN s.history h " +
            "WHERE s.godfather.id = :godfatherId AND h.endDate IS NULL")
    long countActiveSponsorships(@Param("godfatherId") Long godfatherId);

    // 2. Buscar apadrinhamentos recentes (para a lista)
    @Query("SELECT s FROM Sponsorship s " +
            "JOIN FETCH s.pet p " +
            "LEFT JOIN FETCH p.photos " +
            "WHERE s.godfather.id = :godfatherId " +
            "ORDER BY s.createdAt DESC")
    List<Sponsorship> findRecentByGodfather(@Param("godfatherId") Long godfatherId, Pageable pageable);

    // Contar padrinhos distintos ativos
    @Query("SELECT COUNT(DISTINCT s.godfather) FROM Sponsorship s " +
            "JOIN s.history h " +
            "WHERE s.pet.ong.id = :ongId AND h.endDate IS NULL")
    long countActiveGodfathersByOng(@Param("ongId") Long ongId);

    // Calcular receita recorrente atual (soma dos valores mensais ativos)
    @Query("SELECT COALESCE(SUM(h.monthlyAmount), 0) FROM Sponsorship s " +
            "JOIN s.history h " +
            "WHERE s.pet.ong.id = :ongId AND h.endDate IS NULL")
    BigDecimal sumMonthlyRevenueByOng(@Param("ongId") Long ongId);

    // Soma total de receita de apadrinhamentos ativos da ONG
    @Query("SELECT COALESCE(SUM(h.monthlyAmount), 0) FROM Sponsorship s JOIN s.history h " +
            "WHERE s.pet.ong.id = :ongId AND h.endDate IS NULL")
    BigDecimal sumTotalActiveRevenue(@Param("ongId") Long ongId);

    // Soma receita ativa de um PET específico
    @Query("SELECT COALESCE(SUM(h.monthlyAmount), 0) FROM Sponsorship s JOIN s.history h " +
            "WHERE s.pet.id = :petId AND h.endDate IS NULL")
    BigDecimal sumActiveRevenueByPetId(@Param("petId") Long petId);

    // Conta padrinhos ativos de um PET específico
    @Query("SELECT COUNT(s) FROM Sponsorship s JOIN s.history h " +
            "WHERE s.pet.id = :petId AND h.endDate IS NULL")
    long countActiveSponsorsByPetId(@Param("petId") Long petId);

    // Contar cancelamentos neste mês
    @Query("SELECT COUNT(h) FROM SponsorshipHistory h JOIN h.sponsorship s " +
            "WHERE s.pet.ong.id = :ongId " +
            "AND h.status = 'CANCELADO' " +
            "AND MONTH(h.endDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(h.endDate) = YEAR(CURRENT_DATE)")
    long countCancelledThisMonth(@Param("ongId") Long ongId);

    // Somar valor perdido com cancelamentos neste mês
    @Query("SELECT COALESCE(SUM(h.monthlyAmount), 0) FROM SponsorshipHistory h JOIN h.sponsorship s " +
            "WHERE s.pet.ong.id = :ongId " +
            "AND h.status = 'CANCELADO' " +
            "AND MONTH(h.endDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(h.endDate) = YEAR(CURRENT_DATE)")
    BigDecimal sumLostRevenueThisMonth(@Param("ongId") Long ongId);

    // Contar ativos totais da ONG
    @Query("SELECT COUNT(s) FROM Sponsorship s JOIN s.history h " +
            "WHERE s.pet.ong.id = :ongId AND h.endDate IS NULL")
    long countActiveByOng(@Param("ongId") Long ongId);
}