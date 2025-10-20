package com.dnnr.padrinho_digital_api.entities.users;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity(name = "users")
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 11)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_status")
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "user_role")
    private Role role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Métodos para atualização automática do campos createdAt e updatedAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public User(String name, UserStatus status, String email, String password, Role role){
        this.email = email;
        this.password = password;
        this.role = role;
        this.status = status;
        this.name = name;
    }

    // Métodos do Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        switch (this.role) {
            case ADMIN -> {
                return List.of(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_GERENTE"),
                        new SimpleGrantedAuthority("ROLE_VOLUNTARIO"),
                        new SimpleGrantedAuthority("ROLE_PADRINHO")
                );
            }
            case GERENTE -> {
                return List.of(
                        new SimpleGrantedAuthority("ROLE_GERENTE"),
                        new SimpleGrantedAuthority("ROLE_VOLUNTARIO"),
                        new SimpleGrantedAuthority("ROLE_PADRINHO")
                );
            }
            case VOLUNTARIO -> {
                return List.of(
                        new SimpleGrantedAuthority("ROLE_VOLUNTARIO"),
                        new SimpleGrantedAuthority("ROLE_PADRINHO")
                );
            }
            case PADRINHO -> {
                return List.of(
                        new SimpleGrantedAuthority("ROLE_PADRINHO")
                );
            }
            default -> throw new IllegalStateException("Unexpected value: " + this.role);
        }
    }


    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status.equals(UserStatus.ATIVO);
    }
}
