package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class Order {
    private Long id;
    private Long clientId;
    private Long serviceId;
    private String orderDate;
    private String status;

    public Order() {}

    public Order(Long clientId, Long serviceId, String orderDate, String status) {
        this.clientId = clientId;
        this.serviceId = serviceId;
        this.orderDate = orderDate;
        this.status = status;
    }
}