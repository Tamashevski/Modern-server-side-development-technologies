package org.jdbc_lab.statement.model;

import lombok.Data;

@Data
public class Executor {
    private Long id;
    private String firstName;
    private String lastName;
    private String specialization;
    private Integer rating;

    public Executor(){};

    public Executor(final String firstName, final String lastName, final String specialization, final Integer rating) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialization = specialization;
        this.rating = rating;
    }
}
