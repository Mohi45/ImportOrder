package com.cheney.central.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"Item", "Order Number", "Quantity", "Order Date"})
public class ImportEntity {
    public String item;
    public String orderNumber;
    public String quantity;
    public String orderDate;
}

