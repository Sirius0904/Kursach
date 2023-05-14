package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role getRoleByName(String name);
}
