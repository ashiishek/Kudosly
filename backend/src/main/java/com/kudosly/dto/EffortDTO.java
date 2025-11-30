package com.kudosly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EffortDTO {
    private String id;
    private String employeeId;
    private String source;
    private Map<String, Object> payload;
    private String effortType;
    private Integer impactScore;
    private Date timestamp;
}
