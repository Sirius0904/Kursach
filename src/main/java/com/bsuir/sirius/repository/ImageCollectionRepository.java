package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.ImageCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageCollectionRepository extends JpaRepository<ImageCollection, Integer> {
}
