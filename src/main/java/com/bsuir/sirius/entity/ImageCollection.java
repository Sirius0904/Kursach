package com.bsuir.sirius.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "IMAGE_COLLECTION")
public class ImageCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String collectionName;

    @OneToMany(targetEntity = Image.class)
    private Set<Image> images = new HashSet<>();

    @ManyToOne
    private UserData owner;

    private Integer likeCount;
}
