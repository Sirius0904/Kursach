package com.bsuir.sirius.entity;

import com.bsuir.sirius.enumeration.ImageType;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "IMAGE_DATA")
@Accessors(chain = true)
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String path;
    private ImageType imageType;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Commentary> commentaryList = new ArrayList<>();

}
