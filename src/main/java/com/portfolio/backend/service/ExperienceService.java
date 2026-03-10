package com.portfolio.backend.service;

import com.portfolio.backend.entity.Experience;
import com.portfolio.backend.provider.ExperienceDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.portfolio.backend.config.CacheConfig.EXPERIENCES_CACHE;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {

    private final ExperienceDataProvider experienceDataProvider;

    @Cacheable(EXPERIENCES_CACHE)
    public List<Experience> getAllExperiences() {
        return experienceDataProvider.findAllByOrderByDisplayOrder();
    }
}
