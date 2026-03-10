package com.portfolio.backend.provider.file;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portfolio.backend.config.DataSourceProperties;
import com.portfolio.backend.entity.Blog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@Slf4j
public class BlogDataLoader {
    
    private final ResourceLoader resourceLoader;
    private final DataSourceProperties dataSourceProperties;
    private List<Blog> blogs;
    private final ObjectMapper objectMapper;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path writeFilePath;

    public BlogDataLoader(ResourceLoader resourceLoader, DataSourceProperties dataSourceProperties) {
        this.resourceLoader = resourceLoader;
        this.dataSourceProperties = dataSourceProperties;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void loadData() {
        try {
            Resource resource = resourceLoader.getResource(dataSourceProperties.getBlogFilePath());
            blogs = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Blog>>() {});
            log.info("Successfully loaded {} blogs from: {}", blogs.size(), dataSourceProperties.getBlogFilePath());
            
            // Determine write path
            initializeWritePath();
        } catch (IOException e) {
            log.error("Failed to load blogs from: {}", dataSourceProperties.getBlogFilePath(), e);
            log.info("Initializing with empty blog list");
            blogs = new ArrayList<>();
            initializeWritePath();
        }
    }
    
    private void initializeWritePath() {
        String writePath = dataSourceProperties.getEffectiveBlogWritePath();
        
        // If classpath, try to resolve to actual file system path
        if (writePath.startsWith("classpath:")) {
            String relativePath = writePath.substring("classpath:".length());
            // Try src/main/resources first (dev), then current dir
            Path devPath = Paths.get("src/main/resources", relativePath);
            if (Files.exists(devPath)) {
                writeFilePath = devPath;
                log.info("Blog write path resolved to: {}", writeFilePath.toAbsolutePath());
            } else {
                writeFilePath = Paths.get(relativePath);
                log.warn("Using relative path for blog writes: {}", writeFilePath.toAbsolutePath());
            }
        } else {
            writeFilePath = Paths.get(writePath);
            log.info("Using configured blog write path: {}", writeFilePath.toAbsolutePath());
        }
    }

    public List<Blog> getBlogs() {
        lock.readLock().lock();
        try {
            return blogs;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void saveData() {
        if (!dataSourceProperties.isPersistChanges()) {
            log.debug("Persistence disabled, skipping blog file write");
            return;
        }
        
        lock.writeLock().lock();
        try {
            // Ensure parent directory exists
            if (writeFilePath.getParent() != null) {
                Files.createDirectories(writeFilePath.getParent());
            }
            
            // Write with pretty printing
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(writeFilePath.toFile(), blogs);
            
            log.info("Successfully saved {} blogs to: {}", blogs.size(), writeFilePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save blogs to: {}", writeFilePath, e);
            throw new RuntimeException("Failed to persist blogs to file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
