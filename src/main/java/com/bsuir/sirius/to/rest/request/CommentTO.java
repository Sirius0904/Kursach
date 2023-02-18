package com.bsuir.sirius.to.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentTO {
    private String imageId;
    private String text;

    private LocalDateTime date;

    private String author;
    private String profileUrl;

}
