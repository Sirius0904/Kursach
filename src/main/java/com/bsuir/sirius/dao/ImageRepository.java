package com.bsuir.sirius.dao;

import com.bsuir.sirius.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    List<Image> findAllByImageNameContaining(String name);
}
