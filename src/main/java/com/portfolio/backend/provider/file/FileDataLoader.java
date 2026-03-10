package com.portfolio.backend.provider.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.portfolio.backend.config.DataSourceProperties;
import com.portfolio.backend.model.PortfolioData;
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
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "FILE")
@Slf4j
public class FileDataLoader {
    
    private final ResourceLoader resourceLoader;
    private final DataSourceProperties dataSourceProperties;
    private PortfolioData portfolioData;
    private final ObjectMapper objectMapper;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Path writeFilePath;

    public FileDataLoader(ResourceLoader resourceLoader, DataSourceProperties dataSourceProperties) {
        this.resourceLoader = resourceLoader;
        this.dataSourceProperties = dataSourceProperties;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @PostConstruct
    public void loadData() {
        try {
            Resource resource = resourceLoader.getResource(dataSourceProperties.getFilePath());
            portfolioData = objectMapper.readValue(resource.getInputStream(), PortfolioData.class);
            log.info("Successfully loaded portfolio data from: {}", dataSourceProperties.getFilePath());
            
            // Determine write path
            initializeWritePath();
        } catch (IOException e) {
            log.error("Failed to load portfolio data from: {}", dataSourceProperties.getFilePath(), e);
            portfolioData = new PortfolioData();
        }
    }
    
    private void initializeWritePath() {
        String writePath = dataSourceProperties.getEffectiveWritePath();
        
        // If classpath, try to resolve to actual file system path
        if (writePath.startsWith("classpath:")) {
            String relativePath = writePath.substring("classpath:".length());
            // Try src/main/resources first (dev), then current dir
            Path devPath = Paths.get("src/main/resources", relativePath);
            if (Files.exists(devPath)) {
                writeFilePath = devPath;
                log.info("Write path resolved to: {}", writeFilePath.toAbsolutePath());
            } else {
                writeFilePath = Paths.get(relativePath);
                log.warn("Using relative path for writes: {}", writeFilePath.toAbsolutePath());
            }
        } else {
            writeFilePath = Paths.get(writePath);
            log.info("Using configured write path: {}", writeFilePath.toAbsolutePath());
        }
    }

    public PortfolioData getData() {
        lock.readLock().lock();
        try {
            return portfolioData;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public void saveData() {
        if (!dataSourceProperties.isPersistChanges()) {
            log.debug("Persistence disabled, skipping file write");
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
                    .writeValue(writeFilePath.toFile(), portfolioData);
            
            log.info("Successfully saved portfolio data to: {}", writeFilePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to save portfolio data to: {}", writeFilePath, e);
            throw new RuntimeException("Failed to persist data to file", e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
