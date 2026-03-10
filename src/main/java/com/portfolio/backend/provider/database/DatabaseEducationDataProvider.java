package com.portfolio.backend.provider.database;

import com.portfolio.backend.entity.Education;
import com.portfolio.backend.provider.EducationDataProvider;
import com.portfolio.backend.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "portfolio.datasource.type", havingValue = "DATABASE", matchIfMissing = true)
@RequiredArgsConstructor
public class DatabaseEducationDataProvider implements EducationDataProvider {
    
    private final EducationRepository educationRepository;

    @Override
    public List<Education> findAllByOrderByDisplayOrder() {
        return educationRepository.findAllByOrderByDisplayOrder();
    }
}
