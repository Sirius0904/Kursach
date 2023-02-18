package com.bsuir.sirius.to.mvc.response;

import com.bsuir.sirius.entity.Commentary;
import com.bsuir.sirius.to.rest.request.CommentTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DisplayImageTO {
    private String id;
    private String path;
    private String name;
    private String authorImagePath;
    private String description;
    private String author;
    private Integer likes;
    private BigDecimal price;
    private Boolean isSellable;
    private Boolean isOwner;
    private List<Commentary> comments;
}
