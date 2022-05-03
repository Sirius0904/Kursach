package com.bsuir.sirius.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "IMAGE")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String imageName;

    @ManyToOne
    private UserData owner;

    private Boolean isSellable = false;

    private BigDecimal price;

    private Integer likeCount;

    private String description;

    @OneToOne
    private ImageData imageData;
}
