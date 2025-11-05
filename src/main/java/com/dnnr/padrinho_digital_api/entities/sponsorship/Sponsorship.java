package com.dnnr.padrinho_digital_api.entities.sponsorship;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.users.Godfather; // Assumindo esta entidade
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "sponsorship", uniqueConstraints = {
        // Garante que um padrinho só apadrinhe um pet uma vez
        @UniqueConstraint(columnNames = {"pet_id", "godfather_id"})
})
public class Sponsorship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "godfather_id", nullable = false)
    private Godfather godfather;

    // Relação com o histórico de períodos
    @OneToMany(mappedBy = "sponsorship", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("startDate DESC") // Opcional: útil para pegar o mais recente
    private List<SponsorshipHistory> history;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}