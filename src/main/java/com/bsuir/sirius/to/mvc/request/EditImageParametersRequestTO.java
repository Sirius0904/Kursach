package com.bsuir.sirius.to.mvc.request;

import com.bsuir.sirius.enumeration.SellableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditImageParametersRequestTO {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private SellableStatus status;
}
