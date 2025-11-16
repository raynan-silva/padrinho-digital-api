package com.dnnr.padrinho_digital_api.entities.ong;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 450)
    private String street;

    @Column(length = 10)
    private String number;

    @Column(nullable = false, length = 200)
    private String neighborhood;

    @Column(nullable = false, length = 200)
    private String city;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(length = 200)
    private String complement;

    @Column(length = 8)
    private String cep;

    @OneToOne
    @JoinColumn(name = "ong_id")
    private Ong ong;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Address(String street, String number, String neighborhood, String city, String uf, String complement, String cep, Ong ong) {
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.uf = uf;
        this.complement = complement;
        this.cep = cep;
        this.ong = ong;
    }

    public Address(String street, String number, String neighborhood, String city, String uf, String complement, String cep) {
        this.street = street;
        this.number = number;
        this.neighborhood = neighborhood;
        this.city = city;
        this.uf = uf;
        this.complement = complement;
        this.cep = cep;
    }
}
