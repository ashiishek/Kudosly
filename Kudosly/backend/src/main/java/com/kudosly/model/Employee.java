package com.kudosly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employees")
public class Employee {
    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String token;
    private String githubUsername;
    private String slackId;
    private String team;
    private String role;
    private Date joinDate;
    private Boolean isActive;
    private String department;
    private String manager;
    private String avatar;
    private String bio;
    private List<String> skills;
    private Integer recognitionCount;
    private Integer badgeCount;
    private Integer totalEffortScore;
    private Date lastActivityDate;
}
