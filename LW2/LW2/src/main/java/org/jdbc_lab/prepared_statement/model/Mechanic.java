package org.jdbc_lab.prepared_statement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Mechanic {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer experience;
    private String specialization;
    private String phone;
}