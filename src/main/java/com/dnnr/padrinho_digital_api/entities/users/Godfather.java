package com.dnnr.padrinho_digital_api.entities.users;

import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherLevel;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "godfather")
public class Godfather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_level_id")
    private GodfatherLevel currentLevel;

    public Godfather(User user){
        this.user = user;
    }
}
