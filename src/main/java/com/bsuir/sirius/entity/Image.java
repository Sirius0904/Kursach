package com.bsuir.sirius.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
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

    private Integer likeCount = 0;

    private String description;

    @OneToOne
    private ImageData imageData;
}
