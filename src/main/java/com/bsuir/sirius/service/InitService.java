package com.bsuir.sirius.service;

import com.bsuir.sirius.entity.Role;
import com.bsuir.sirius.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitService {
    private final RoleRepository roleRepository;


    public void init() {
        if (roleRepository.findAll().isEmpty()) {
            log.info("Roles DB is not initialized, initing database with default data");

            Role admin = new Role().setName("ADMIN");
            Role user = new Role().setName("USER");

            roleRepository.saveAndFlush(admin);
            roleRepository.saveAndFlush(user);

            log.info("Initialized roles {}", List.of(admin, user));
        }

    }
}