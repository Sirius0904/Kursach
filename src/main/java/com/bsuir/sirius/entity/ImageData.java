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
@Table(name = "IMAGE_DATA")
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @OneToOne
    private Image image;

    @ManyToOne
    @JoinColumn(name = "user")
    private User owner;

    private BigDecimal price;
    private Integer likeCount;
}
