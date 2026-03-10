package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.Experience;
import com.portfolio.backend.provider.ExperienceDataProvider;
import com.portfolio.backend.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseExperienceDataProvider implements ExperienceDataProvider {
    
    private final ExperienceRepository experienceRepository;

    @Override
    public List<Experience> findAllByOrderByDisplayOrder() {
        return experienceRepository.findAllByOrderByDisplayOrder();
    }
}
