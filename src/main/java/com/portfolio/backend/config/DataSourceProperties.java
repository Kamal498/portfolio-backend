package com.portfolio.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "portfolio.datasource")
@Data
public class DataSourceProperties {
    
    private SourceType type = SourceType.DATABASE;
    
    private String filePath = "classpath:portfolio-data.json";
    
    private String blogFilePath = "classpath:blogs.json";
    
    private boolean persistChanges = true;
    
    private String writeFilePath;
    
    private String writeBlogFilePath;
    
    public enum SourceType {
        DATABASE,
        FILE
    }
    
    public String getEffectiveWritePath() {
        return writeFilePath != null ? writeFilePath : filePath;
    }
    
    public String getEffectiveBlogWritePath() {
        return writeBlogFilePath != null ? writeBlogFilePath : blogFilePath;
    }
}
