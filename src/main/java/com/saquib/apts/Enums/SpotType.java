package com.saquib.apts.Enums;

import lombok.Getter;

@Getter
public enum SpotType {
    COMPACT(10.0),
    LARGE(20.0),
    HANDICAPPED(5.0);

    private final  double price;

    SpotType(double price){
        this.price = price;
    }


}
