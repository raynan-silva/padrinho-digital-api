package com.dnnr.padrinho_digital_api.entities.godfather;

import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "godfather_seal")
@Getter
@NoArgsConstructor
public class GodfatherSeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "godfather_id", nullable = false)
    private Godfather godfather;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seal_id", nullable = false)
    private Seal seal;

    @CreationTimestamp
    @Column(name = "conquered_at", nullable = false, updatable = false)
    private LocalDateTime conqueredAt;

    public GodfatherSeal(Godfather godfather, Seal seal) {
        this.godfather = godfather;
        this.seal = seal;
    }
}