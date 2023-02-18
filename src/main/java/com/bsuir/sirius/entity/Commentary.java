package com.bsuir.sirius.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "COMMENTS")
public class Commentary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String text;

    @OneToOne
    private User author;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private ImageData imageData;

    private LocalDateTime dateTime = LocalDateTime.now();

}
