package com.emirates.flights.controller.ro;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
@ToString
@Getter
@Setter
@AllArgsConstructor
public class PriceResponseRO {
    private BigDecimal price;
}
