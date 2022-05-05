package com.bsuir.sirius.to.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DisplayImageTO {
    private String path;
    private String name;
    private String authorImagePath;
    private String description;
    private String author;
    private Integer likes;
    private BigDecimal price;
    private Boolean isSellable;
}
