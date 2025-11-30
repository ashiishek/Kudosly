package com.kudosly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecognitionDTO {
    private String id;
    private String employeeId;
    private String effortId;
    private String message;
    private String badge;
    private Integer impactScore;
    private String category;
    private Date timestamp;
}
