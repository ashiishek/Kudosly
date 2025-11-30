package com.kudosly.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadgeDTO {
    private String badgeId;
    private String name;
    private String description;
    private String icon;
    private Map<String, Object> criteria;
}
