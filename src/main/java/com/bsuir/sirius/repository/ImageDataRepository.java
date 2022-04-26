package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Integer> {
    List<ImageData> findAllByImageImageName(String imageName);
    List<ImageData> findAllByOwnerUsername(String username);
}
