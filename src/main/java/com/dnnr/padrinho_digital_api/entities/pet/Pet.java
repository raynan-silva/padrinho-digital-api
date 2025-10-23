package com.dnnr.padrinho_digital_api.entities.pet;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "pet")
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetStatus status;

    @Column(nullable = false, length = 50)
    private String breed;

    @Column(nullable = false)
    private double weight;

    @Column(name = "date_of_admission", nullable = false)
    private LocalDate dateOfAdmission;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetGender gender;

    @Column(nullable = false)
    private String profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id")
    private Ong ong;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Pet(String name, LocalDate birthDate, PetStatus status, String breed, double weight, LocalDate dateOfAdmission, PetGender gender, String profile, Ong ong) {
        this.name = name;
        this.birthDate = birthDate;
        this.status = status;
        this.breed = breed;
        this.weight = weight;
        this.dateOfAdmission = dateOfAdmission;
        this.gender = gender;
        this.profile = profile;
        this.ong = ong;
    }
}
