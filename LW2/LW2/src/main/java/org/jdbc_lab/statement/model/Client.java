package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class Client {
    private Long id;
    private String name;
    private String phone;
    private String email;

    public Client() {}

    public Client(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
