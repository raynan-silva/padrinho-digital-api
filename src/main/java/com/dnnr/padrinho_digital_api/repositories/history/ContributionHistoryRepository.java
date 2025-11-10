package com.dnnr.padrinho_digital_api.repositories.history;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.Donation;
import com.dnnr.padrinho_digital_api.repositories.projection.ContributionHistoryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

// Você pode fazer este repositório estender JpaRepository de qualquer entidade,
// (ex: Godfather ou Ong) ou apenas ser uma interface.
// Vamos usar uma entidade fictícia só para o Spring implementá-lo.
public interface ContributionHistoryRepository extends JpaRepository<Donation, Long> { // Pode ser qualquer entidade

    // --- Consulta Base (Comum para todos) ---
    // (Atenção: A sintaxe SQL é longa e precisa)
    String BASE_SPONSORSHIP_QUERY = """
        SELECT
            CONCAT('s-', sh.id) AS id,
            sh.start_date AS date,
            p.name AS title,
            p.breed AS subtitle,
            sh.monthly_amount AS amount,
            'Apadrinhamento' AS method,
            sh.status AS status
        FROM sponsorship_history sh
        JOIN sponsorship s ON sh.sponsorship_id = s.id
        JOIN pet p ON s.pet_id = p.id
    """;

    String BASE_DONATION_QUERY = """
        SELECT
            CONCAT('d-', d.id) AS id,
            d.date AS date,
            dc.title AS title,
            'Doação para Campanha' AS subtitle,
            d.amount AS amount,
            'Doação' AS method,
            'CONCLUIDO' AS status
        FROM donation d
        JOIN donation_campaign dc ON d.donation_campaign_id = dc.id
    """;

    // --- Regra 1: Padrinho ---
    String PADRINHO_QUERY = BASE_SPONSORSHIP_QUERY + " WHERE s.godfather_id = :godfatherId" +
            " UNION ALL " +
            BASE_DONATION_QUERY + " WHERE d.godfather_id = :godfatherId";

    @Query(value = PADRINHO_QUERY,
            countQuery = "SELECT COUNT(*) FROM (" + PADRINHO_QUERY + ") AS combined",
            nativeQuery = true)
    Page<ContributionHistoryProjection> findHistoryForGodfather(@Param("godfatherId") Long godfatherId, Pageable pageable);


    // --- Regra 2: ONG ---
    String ONG_QUERY = BASE_SPONSORSHIP_QUERY + " WHERE p.ong_id = :ongId" +
            " UNION ALL " +
            BASE_DONATION_QUERY + " WHERE dc.ong_id = :ongId";

    @Query(value = ONG_QUERY,
            countQuery = "SELECT COUNT(*) FROM (" + ONG_QUERY + ") AS combined",
            nativeQuery = true)
    Page<ContributionHistoryProjection> findHistoryForOng(@Param("ongId") Long ongId, Pageable pageable);


    // --- Regra 3: Admin ---
    String ADMIN_QUERY = BASE_SPONSORSHIP_QUERY + " UNION ALL " + BASE_DONATION_QUERY;

    @Query(value = ADMIN_QUERY,
            countQuery = "SELECT COUNT(*) FROM (" + ADMIN_QUERY + ") AS combined",
            nativeQuery = true)
    Page<ContributionHistoryProjection> findHistoryForAdmin(Pageable pageable);
}