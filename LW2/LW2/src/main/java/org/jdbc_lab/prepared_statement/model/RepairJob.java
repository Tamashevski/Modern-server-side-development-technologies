package org.jdbc_lab.prepared_statement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairJob {
    private Long id;
    private Long carId;
    private Long mechanicId;
    private String repairDate;
    private Integer cost;
}