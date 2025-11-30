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
@Document(collection = "weekly_digests")
public class WeeklyDigest {
    @Id
    private String id;
    private String employeeId;
    private Date weekStart;
    private Date weekEnd;
    private Date weekStartDate;
    private Date weekEndDate;
    private String summary;
    private String narrative;
    private List<String> topRecognitions;
    private Double collaborationScore;
    private List<String> learningWins;
    private List<String> badgesEarned;
    private List<String> highlights;
    private Map<String, Object> metrics;
    private List<String> topContributors;
    private Integer totalEfforts;
    private Integer totalRecognitions;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isPublished;
}
