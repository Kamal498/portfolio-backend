package com.portfolio.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private String image;
    private List<String> tags;
    private String githubUrl;
    private String demoUrl;
    private Boolean featured;
}
