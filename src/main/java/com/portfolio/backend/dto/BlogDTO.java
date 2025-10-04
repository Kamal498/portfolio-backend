package com.portfolio.backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogDTO {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String author;
    private LocalDateTime date;
    private List<String> tags;
    private String readTime;
    private Boolean published;
}
