package com.kudosly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "efforts")
public class Effort {
    @Id
    private String id;
    private String employeeId;
    private String source; // jira, git, slack, lms, calendar
    private String effortType;
    private Integer impactScore;
    private Date timestamp;
    private Date createdAt;
    private Date updatedAt;
    private Map<String, Object> payload;
    private String category;
    private String status;
    private Boolean isPublic;
    private List<Map<String, Object>> comments;
    private Integer likes;
    private Integer shares;
}
