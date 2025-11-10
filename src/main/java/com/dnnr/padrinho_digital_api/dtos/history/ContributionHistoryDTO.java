package com.dnnr.padrinho_digital_api.dtos.history;

import com.dnnr.padrinho_digital_api.repositories.projection.ContributionHistoryProjection;

import java.math.BigDecimal;
import java.time.LocalDate;

// Este DTO corresponde à interface 'Donation' do seu frontend
public record ContributionHistoryDTO(
        String id,          // ID unificado (ex: "s-1" para apadrinhamento, "d-1" para doação)
        LocalDate date,     // Data da transação
        String title,       // Nome do Pet (se apadrinhamento) OU Título da Campanha (se doação)
        String subtitle,    // Raça do Pet (se apadrinhamento) OU "Doação para Campanha"
        BigDecimal amount,
        String method,      // "Apadrinhamento" ou "Doação"
        String status        // "ATIVO", "CANCELADO" (apadrinhamento) ou "CONCLUIDO" (doação)
) {
    // Construtor manual para mapear da Projeção do Repositório
    public ContributionHistoryDTO(ContributionHistoryProjection projection) {
        this(
                projection.getId(),
                projection.getDate(),
                projection.getTitle(),
                projection.getSubtitle(),
                projection.getAmount(),
                projection.getMethod(),
                projection.getStatus()
        );
    }
}