package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.User;
import com.bsuir.sirius.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData, Integer> {
    UserData getUserDataByBaseUserUsername(String username);
}
