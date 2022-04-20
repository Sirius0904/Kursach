package com.bsuir.sirius.dao;

import com.bsuir.sirius.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserData, Integer> {
}
