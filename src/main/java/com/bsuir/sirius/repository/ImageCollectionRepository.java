package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.ImageCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageCollectionRepository extends JpaRepository<ImageCollection, Integer> {
    List<ImageCollection> findAllByCollectionNameContaining(String collectionName);
    List<ImageCollection> findAllByThemeContaining(String theme);
}
