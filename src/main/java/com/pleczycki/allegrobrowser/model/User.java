package com.pleczycki.allegrobrowser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@NoArgsConstructor
@Data
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "email"
        })
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private boolean enabled;

    private String registrationToken;

    private String passRecoveryToken;

    @Column(nullable = false)
    @NotNull
    private Date createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(User other) {
        this.setId(other.getId());
        this.setUsername(other.getUsername());
        this.setCreatedAt(other.getCreatedAt());
        this.setPassRecoveryToken(other.getPassRecoveryToken());
        this.setRegistrationToken(other.getRegistrationToken());
        this.setPassword(other.getPassword());
        this.setEnabled(other.isEnabled());
        this.setEmail(other.getEmail());
        this.setRoles(other.getRoles());
    }
}