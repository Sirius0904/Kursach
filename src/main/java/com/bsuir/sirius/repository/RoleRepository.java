package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role getRoleByNameIgnoreCase(String name);
}
