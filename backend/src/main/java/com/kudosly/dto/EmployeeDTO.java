package com.kudosly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private String id;
    private String name;
    private String email;
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
