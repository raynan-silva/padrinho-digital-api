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
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Admin(User user, Ong ong) {
        this.user = user;
    }
}
