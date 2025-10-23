package com.dnnr.padrinho_digital_api.entities.users;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ong_id", nullable = false, unique = true)
    private Ong ong;

    public Volunteer(User user, Ong ong) {
        this.user = user;
        this.ong = ong;
    }
}
