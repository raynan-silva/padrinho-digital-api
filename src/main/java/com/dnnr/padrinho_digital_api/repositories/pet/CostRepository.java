package com.dnnr.padrinho_digital_api.repositories.pet;

import com.dnnr.padrinho_digital_api.dtos.report.ExpenseDistributionDTO;
import com.dnnr.padrinho_digital_api.entities.pet.Cost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CostRepository extends JpaRepository<Cost, Long> {
    List<Cost> findAllByPetId(Long petId);

    @Query("SELECT COALESCE(SUM(ch.monthlyAmount), 0) FROM Cost c " +
            "JOIN c.history ch " +
            "WHERE c.pet.ong.id = :ongId " +
            "AND ch.endDate IS NULL")
    BigDecimal sumTotalActiveCostsByOng(@Param("ongId") Long ongId);

    @Query("SELECT new com.dnnr.padrinho_digital_api.dtos.report.ExpenseDistributionDTO(ch.name, SUM(ch.monthlyAmount)) " +
            "FROM Cost c JOIN c.history ch " +
            "WHERE c.pet.ong.id = :ongId AND ch.endDate IS NULL " +
            "GROUP BY ch.name")
    List<ExpenseDistributionDTO> findExpenseDistributionByOng(@Param("ongId") Long ongId);

    // Soma total de despesas da ONG
    @Query("SELECT COALESCE(SUM(ch.monthlyAmount), 0) FROM Cost c JOIN c.history ch " +
            "WHERE c.pet.ong.id = :ongId AND ch.endDate IS NULL")
    BigDecimal sumTotalActiveExpenses(@Param("ongId") Long ongId);
}
