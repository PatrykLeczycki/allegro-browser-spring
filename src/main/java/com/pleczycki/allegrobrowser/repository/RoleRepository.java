package com.pleczycki.allegrobrowser.repository;

import com.pleczycki.allegrobrowser.model.Role;
import com.pleczycki.allegrobrowser.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}