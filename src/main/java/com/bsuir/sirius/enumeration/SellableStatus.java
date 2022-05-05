package com.bsuir.sirius.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SellableStatus {
    YES(true),
    NO(false);

    private final Boolean value;
}
