package com.dnnr.padrinho_digital_api.entities.godfather;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "seal")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url", nullable = false)
    private String iconUrl;

    @Column(name = "trigger_metric", nullable = false, length = 50)
    private String triggerMetric; // Ex: "DONATION_COUNT"

    @Column(name = "trigger_value", nullable = false)
    private BigDecimal triggerValue;
}