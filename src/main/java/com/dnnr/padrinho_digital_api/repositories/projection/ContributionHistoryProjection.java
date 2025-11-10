package com.dnnr.padrinho_digital_api.repositories.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

// Interface para mapear o resultado da Native Query
public interface ContributionHistoryProjection {
    String getId();
    LocalDate getDate();
    String getTitle();
    String getSubtitle();
    BigDecimal getAmount();
    String getMethod();
    String getStatus();
}