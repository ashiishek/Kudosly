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
@Document(collection = "recognitions")
public class Recognition {
    @Id
    private String id;
    private String employeeId;
    private String effortId;
    private String message;
    private String badge;
    private Integer impactScore;
    private String category;
    private Date timestamp;
    private Date createdAt;
    private Date updatedAt;
    private Integer likes;
    private Integer shares;
}
