package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class Service {
    private Long id;
    private String title;
    private Long executorId;
    private Integer price;
    private String category;

    public Service() {}

    public Service(String title, Long executorId, Integer price, String category) {
        this.title = title;
        this.executorId = executorId;
        this.price = price;
        this.category = category;
    }
}