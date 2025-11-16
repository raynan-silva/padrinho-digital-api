package com.dnnr.padrinho_digital_api.entities.godfather;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "godfather_level")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GodfatherLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "icon_url", nullable = false)
    private String iconUrl;

    @Column(name = "level_order", nullable = false, unique = true)
    private Integer order;

    @Column(name = "required_amount", nullable = false)
    private BigDecimal requiredAmount;
}
