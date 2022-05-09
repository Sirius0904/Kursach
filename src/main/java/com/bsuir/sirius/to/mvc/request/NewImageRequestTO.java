package com.bsuir.sirius.to.mvc.request;

import com.bsuir.sirius.enumeration.SellableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewImageRequestTO {
    private String name;
    private String description;
    private BigDecimal price;
    private SellableStatus status;
    private MultipartFile image;
}
