package com.kudosly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee_badges")
public class EmployeeBadge {
    @Id
    private String id;
    private String employeeId;
    private String badgeId;
    private Date earnedDate;
    private Integer progressPercentage;
}
