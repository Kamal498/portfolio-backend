package com.portfolio.backend.service;

import com.portfolio.backend.entity.Education;
import com.portfolio.backend.provider.EducationDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.portfolio.backend.config.CacheConfig.EDUCATION_CACHE;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationDataProvider educationDataProvider;

    @Cacheable(EDUCATION_CACHE)
    public List<Education> getAllEducation() {
        return educationDataProvider.findAllByOrderByDisplayOrder();
    }
}
