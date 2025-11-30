package com.kudosly.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "badges")
public class Badge {
    @Id
    private String id;  // Maps to MongoDB's _id
    
    @Field("badgeId")
    private String badgeId;  // Maps to badgeId field
    
    private String name;
    private String description;
    private String icon;
    private String rarity;
    private Integer points;
    private String color;
    private String category;
    private String unlockCondition;
    private Map<String, Object> requirements;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isActive;
    private Integer displayOrder;
    private Map<String, Object> criteria;
}
