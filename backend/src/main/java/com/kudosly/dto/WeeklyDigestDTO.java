package com.kudosly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyDigestDTO {
    private String id;
    private String employeeId;
    private Date weekStart;
    private Date weekEnd;
    private String summary;
    private List<RecognitionDTO> topRecognitions;
    private Double collaborationScore;
    private List<String> learningWins;
    private List<BadgeDTO> badgesEarned;
}
